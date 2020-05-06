package app.controllers;

import app.models.LinesLoader;
import app.models.TripSimulation;
import app.models.maps.*;
import app.view.BaseGui;

import java.time.LocalTime;
import java.util.*;

import static java.time.temporal.ChronoUnit.MILLIS;

public class Simulator {

    // Simulation
    private Timer timer;
    private boolean simulationState = false;
    private LocalTime simulationTime;
    private int simulationSpeed = 1000; //ms
    private boolean simulationTask = false;
    private LocalTime previousRealTime;
    private int simulationRefreshSpeed = 10; //s

    // Model
    private StreetMap streetMap;
    private List<Line> lines = new ArrayList<>();

    // View
    private BaseGui gui;


    public Simulator(StreetMap streetMap, BaseGui gui) throws Exception {
        this.streetMap = streetMap;
        this.gui = gui;
        this.lines = LinesLoader.load(this.streetMap);
    }

    private int minusLocalTime(LocalTime diff1, LocalTime diff2)
    {
        LocalTime diff = diff1.minusHours(diff2.getHour())
                .minusMinutes(diff2.getMinute())
                .minusSeconds(diff2.getSecond());

        return Math.abs((diff.getHour() * 60 * 60) + (diff.getMinute() * 60) + (diff.getSecond()));
    }


    private void handleTrip(Trip trip, Line line) {
        System.out.println("handleBus:" + trip.getId());

        if (trip.getTimetable().isEmpty()) {
            return;
        }
        // Is this connection active at current time?
        LocalTime tmp1 = trip.getTimetable().get(0);
        LocalTime tmp2 = trip.getTimetable().get(trip.getTimetable().size() - 1);

        if (simulationTime.isBefore(tmp1) || simulationTime.isAfter(tmp2)) {
            return;
        }

        List<LocalTime> timeTable = trip.getTimetable();
        for (int i = 0; i < timeTable.size() - 1; i++) {
            LocalTime firstTime = timeTable.get(i);
            LocalTime secondTime = timeTable.get(i + 1);
            if (!(simulationTime.isBefore(firstTime) || simulationTime.isAfter(secondTime))) {
                Coordinate currentTripPosition = TripSimulation.dotPosition(this.simulationTime, trip.getTimetable().get(i), trip.getTimetable().get(i + 1), line.getStopByIndex(i), line.getStopByIndex(i + 1), line);
                this.gui.createDot(currentTripPosition);
                break;
            }
        }
    }

    private void handleLine(Line line) {
        for (Trip trip : line.getLineConnections()) {
            handleTrip(trip, line);
        }
    }

    private void simulationRefresh() {
        gui.clearSimulationGui();

        System.out.println("Refresh simulation...");
        for (Line line : lines) {
            handleLine(line);
        }
    }

    private void simulationHandle() {
        simulationTime = simulationTime.plus(this.simulationSpeed, MILLIS);
    }


    /**
     * Start simulation at realtime
     */
    public void start() {
        start(LocalTime.now());
    }

    /**
     * Start simulation at specified time
     */
    public void start(LocalTime time) {

        System.out.println(lines.get(0).toString());

        if (!simulationState) {
            final TimerTask timerTask = new TimerTask() {
                @Override
                public void run() {
                    if (!simulationTask) {
                        simulationTask = true;
                        gui.showTime(simulationTime);
                        if (previousRealTime == null) {
                            simulationRefresh();
                            previousRealTime = LocalTime.now();
                            simulationTask = false;
                            return;
                        }
                        LocalTime diff = LocalTime.now().minusHours(previousRealTime.getHour())
                                .minusMinutes(previousRealTime.getMinute())
                                .minusSeconds(previousRealTime.getSecond());

                        int actualSeconds = (diff.getHour() * 60 * 60) + (diff.getMinute() * 60) + (diff.getSecond());
                        if (actualSeconds >= simulationRefreshSpeed) {
                            simulationRefresh();
                            previousRealTime = LocalTime.now();
                        }
                        simulationHandle();
                        simulationTask = false;
                    }
                }
            };
            this.simulationTime = time;
            this.simulationState = true;
            this.timer = new Timer("Simulator");
            timer.schedule(timerTask, 0, 1000);

            System.err.println("Simulation started.");
        } else {
            System.err.println("Simulation already running...");
        }


    }

    /**
     * Set simulation tick speed in ms. Refresh of GUI is every 10 ticks.
     * Implicitly restarts simulation.
     *
     * @param simulationSpeed
     */
    public void setSimulationSpeed(int simulationSpeed) {
        if (this.simulationState) {
            stop();
            this.simulationSpeed = 1000 * simulationSpeed;
            start(this.simulationTime);
        } else {
            this.simulationSpeed = 1000 * simulationSpeed;
        }
    }

    public void setSimulationRefreshSpeed(int simulationRefreshSpeed) throws Exception {
        if(simulationRefreshSpeed<1)
        {
            throw new Exception("Simulation refresh speed cant be lower than 1!");
        }
        if (this.simulationState) {
            stop();
            this.simulationRefreshSpeed = simulationRefreshSpeed;
            start(this.simulationTime);
        } else {
            this.simulationRefreshSpeed = simulationRefreshSpeed;
        }
    }

    /**
     * Return simulation state as boolean.
     * True...Running
     * False...Stopped
     *
     * @return
     */
    public boolean getSimulationState() {
        return this.simulationState;
    }

    /**
     * Stop simulation.
     */
    public void stop() {
        this.simulationState = false;
        if (this.timer != null) {
            this.previousRealTime =null;
            this.timer.cancel();
            this.timer.purge();
            System.err.println("Simulation stopped.");
        }

    }

    /**
     * Set simulation time to specified string in format HH:mm:ss
     * Cant set time while simulation is running.
     *
     * @param time
     */
    public void setSimulationTime(String time) throws Exception {
        if (!simulationState) {
            simulationTime = LocalTime.parse(time);
        } else {
            throw new Exception("Can't change simulation parameters while running!");
        }
    }

}
