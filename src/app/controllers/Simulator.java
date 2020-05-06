package app.controllers;

import app.models.LinesLoader;
import app.models.TripSimulation;
import app.models.maps.*;
import app.view.BaseGui;
import app.models.CSVLoader;
import javafx.application.Platform;

import java.time.LocalTime;
import java.time.temporal.TemporalField;
import java.util.*;

import static java.time.temporal.ChronoUnit.MILLIS;

public class Simulator {

    // Simulation
    private Timer timer;
    private boolean simulationState = false;
    private LocalTime simulationTime;
    private int simulationSpeed = 1000; //ms
    private boolean simulationTask = false;
    private LocalTime previousSimulationTime;

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
        gui.showTime(simulationTime);
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

                        if (previousSimulationTime == null) {
                            simulationRefresh();
                            gui.showTime(simulationTime);
                            previousSimulationTime = LocalTime.of(simulationTime.getHour(), simulationTime.getMinute(), simulationTime.getSecond(), simulationTime.getNano());
                            simulationTask = false;
                            return;
                        }
                        LocalTime diff = simulationTime.minusHours(previousSimulationTime.getHour())
                                .minusMinutes(previousSimulationTime.getMinute())
                                .minusSeconds(previousSimulationTime.getSecond());

                        int actualSeconds = (diff.getHour() * 60 * 60) + (diff.getMinute() * 60) + (diff.getSecond());
                        if (actualSeconds >= 10) {
                            simulationRefresh();
                            previousSimulationTime = LocalTime.of(simulationTime.getHour(), simulationTime.getMinute(), simulationTime.getSecond(), simulationTime.getNano());
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
