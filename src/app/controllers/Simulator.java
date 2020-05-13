package app.controllers;

import app.models.LinesLoader;
import app.models.TripSimulation;
import app.models.maps.*;
import app.view.BaseGui;
import javafx.event.EventHandler;
import javafx.scene.input.MouseEvent;
import javafx.scene.shape.Circle;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import static java.time.temporal.ChronoUnit.MILLIS;

/**
 * Simulator class that handle all operations about simulation.
 * @author Petr Křehlík, Martin Klobušický
 * @date 13.5.2020
 */
public class Simulator {

    // Simulation
    private Timer timer;
    public Timer permanentTimer;
    private boolean simulationState = false;
    private LocalTime simulationTime;
    private int simulationSpeed = 1000; //ms
    private boolean simulationTask = false;
    private LocalTime previousRealTime;
    private int simulationRefreshSpeed = 1; //s
    private Trip selectedTrip;

    // Model
    private StreetMap streetMap;
    private List<Line> lines = new ArrayList<>();

    // View
    private BaseGui gui;

    /**
     * Get list of all lines.
     * @return List of all lines.
     */
    public List<Line> getLines() {
        return lines;
    }

    /**
     * Initialize simulator.
     * Start permanent timer (1 sec).
     * @param streetMap Full street map.
     * @param gui GUI controller.
     * @throws Exception When error.
     */
    public Simulator(StreetMap streetMap, BaseGui gui) throws Exception {
        this.streetMap = streetMap;
        this.gui = gui;
        this.lines = LinesLoader.load(this.streetMap);
        this.computeTraffic();

        this.permanentTimer = new Timer("1 sec-permanent");
        this.permanentTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                highlightCounter++;
                if (highlightCounter == 3) {
                    gui.clearHighlight();
                }
            }
        }, 0, 1000);
    }

    /**
     * Compute difference between two times.
     * @param diff1 Time 1.
     * @param diff2 Time 2.
     * @return Difference between two time in seconds.
     */
    private int minusLocalTime(LocalTime diff1, LocalTime diff2) {
        LocalTime diff = diff1.minusHours(diff2.getHour())
                .minusMinutes(diff2.getMinute())
                .minusSeconds(diff2.getSecond());

        return (diff.getHour() * 60 * 60) + (diff.getMinute() * 60) + (diff.getSecond());
    }


    private int highlightCounter = 0;

    /**
     * Core simulator function.
     * Determine if specific trip on line is active at current simulation time.
     * Draw vehicle position in map.
     * @param trip Trip.
     * @param line Line.
     */
    private void handleTrip(Trip trip, Line line) {

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
                Circle circle = this.gui.createDot(currentTripPosition);
                trip.setCircle(circle);
                circle.setOnMouseEntered(new EventHandler<MouseEvent>() {
                    /**
                     * Event when mouse hover on vehicle position.
                     * Highlight line and stops names.
                     * Show vehicle timetable.
                     * @param event
                     */
                    @Override
                    public void handle(MouseEvent event) {
                        gui.highlightLine(line);
                        highlightCounter = 0;
                        gui.clearTripTimetable();
                        gui.showTripTimetable(trip);
                        selectedTrip = trip;
                    }
                });
                this.gui.addActiveVehicle(trip);
                break;
            }
        }
    }

    /**
     * Core simulator function.
     * Handle all existing lines.
     * @param line
     */
    private void handleLine(Line line) {
        for (Trip trip : line.getLineConnections()) {
            handleTrip(trip, line);
        }
    }

    /**
     * Core simulator function.
     * Called every X seconds to refresh position of vehicles.
     */
    private void simulationRefresh() {
        gui.clearSimulationGui();
        gui.clearActiveVehicles();
        for (Line line : lines) {
            handleLine(line);
        }
    }

    /**
     * Core simulator function.
     * Called every second to update simulation time.
     */
    private void simulationHandle() {
        simulationTime = simulationTime.plus(this.simulationSpeed, MILLIS);
    }

    /**
     * Compute new timetable with traffic on streets. Wont work when simulation is running.
     * @throws Exception When simulation running.
     */
    public void computeTraffic() throws Exception {

        if (!getSimulationState()) {
            for (Line line : this.lines) {
                line.resetTimetable();
            }
            for (Street street : this.streetMap.getStreets()) {
                if (street.getTrafficCoefficient() > 1) {
                    trafficCore(street);
                }
            }
        } else {
            throw new Exception("Can't change street settings while simulation is running!");
        }


    }

    /**
     * Update timetable when traffic is higher for specific street and lines on that street.
     * @param street Street to compute.
     */
    private void trafficCore(Street street) {
        List<Line> returnLines = new ArrayList<>();
        List<Stop> lineStops = new ArrayList<>();
        for (int i = 0; i < this.lines.size(); i++) {
            if (this.lines.get(i).getStreets().contains(street)) {
                returnLines.add(this.lines.get(i));
            }
        }

        for (Line line : returnLines) {
            lineStops = line.getRealStops();
            for (int i = 0; i < lineStops.size() - 1; i++) {
                List<Street> streetsBetween = line.getStreetsBetween(lineStops.get(i), lineStops.get(i + 1));
                if (streetsBetween.size() == 1) {
                    if (streetsBetween.get(0).equals(street)) {
                        double lenghtOfStreet = line.getLenghtOfStreet(street);
                        double lenghtOfStops = line.getStopsLength(line.getStopByIndex(i), line.getStopByIndex(i + 1));
                        for (int k = 0; k < line.getTrips().size(); k++) {
                            List<LocalTime> times = line.getTrips().get(k).getActualTimetable();
                            LocalTime first = times.get(i);
                            LocalTime second = times.get(i + 1);
                            int sumTime = minusLocalTime(second, first);
                            long trafficTime = Math.round((double) (lenghtOfStreet / lenghtOfStops * sumTime) * street.getTrafficCoefficient() - (lenghtOfStreet / lenghtOfStops * sumTime));
                            for (int t = i + 1; t < times.size(); t++) {
                                LocalTime newTime = times.get(t);
                                newTime = newTime.plusSeconds(trafficTime);
                                times.set(t, newTime);
                            }
                        }
                    }
                } else if (streetsBetween.contains(street)) {
                    for (int j = 0; j < streetsBetween.size(); j++) {
                        if (streetsBetween.get(j).equals(street)) {
                            if (lineStops.get(i).getStreet().equals(street)) {
                                Coordinate follow = line.followPoint(streetsBetween.get(j), streetsBetween.get(j + 1));
                                calculateNewTime(street, lineStops, line, i, follow);
                            } else if (lineStops.get(i + 1).getStreet().equals(street)) {
                                Coordinate follow = line.followPoint(streetsBetween.get(j - 1), streetsBetween.get(j));
                                calculateNewTime(street, lineStops, line, i, follow);
                            } else { //malo by fungovať aj ako ošetrenie ak sú na rovnakej ulici
                                double lenghtOfStreet = line.getLenghtOfStreet(street);
                                double lenghtOfStops = line.getStopsLength(line.getStopByIndex(i), line.getStopByIndex(i + 1));
                                for (int k = 0; k < line.getTrips().size(); k++) {
                                    List<LocalTime> times = line.getTrips().get(k).getActualTimetable();
                                    LocalTime first = times.get(i);
                                    LocalTime second = times.get(i + 1);
                                    int sumTime = minusLocalTime(second, first);
                                    long trafficTime = Math.round((double) (lenghtOfStreet / lenghtOfStops * sumTime) * street.getTrafficCoefficient() - (lenghtOfStreet / lenghtOfStops * sumTime));
                                    for (int t = i + 1; t < times.size(); t++) {
                                        LocalTime newTime = times.get(t);
                                        newTime = newTime.plusSeconds(trafficTime);
                                        times.set(t, newTime);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * Calculate new time of stops in trips.
     * @param street Street.
     * @param lineStops List of line stops.
     * @param line Line object.
     * @param i Current position.
     * @param follow Following coordinate.
     */
    private void calculateNewTime(Street street, List<Stop> lineStops, Line line, int i, Coordinate follow) {
        Stop stoptmp = new MyStop("tmp", follow);
        stoptmp.setStreet(street);
        double tmp = line.getStopAndCoordinateLength(lineStops.get(i), stoptmp);
        double length = line.getStopAndCoordinateLength(lineStops.get(i), lineStops.get(i + 1));
        for (int k = 0; k < line.getTrips().size(); k++) {
            List<LocalTime> times = line.getTrips().get(k).getActualTimetable();
            LocalTime first = times.get(i);
            LocalTime second = times.get(i + 1);
            int sumTime = minusLocalTime(second, first);
            long trafficTime = Math.round((double) (tmp / length * sumTime) * street.getTrafficCoefficient() - (tmp / length * sumTime));
            for (int t = i + 1; t < times.size(); t++) {
                LocalTime newTime = times.get(t);
                newTime = newTime.plusSeconds(trafficTime);
                times.set(t, newTime);
            }
        }
    }

    /**
     * @return True if some line has conflict.
     */
    public boolean isConflict()
    {
        for(Line line:this.lines){
            if(line.isConflict())
            {
                return true;
            }
        }
        return false;
    }

    /**
     * Compute conflicts for all lines.
     */
    public void setLinesBlock() {
        for (Line line : this.getLines()) {
            line.computeConflicts();
        }
    }


    /**
     * Start simulation at current real time.
     */
    public void start() throws Exception {
        start(LocalTime.now());
    }

    /**
     * Start simulation at specified time
     */
    public void start(LocalTime time) {

        if (!simulationState) {

            // Main Timer Task - called every 1 sec
            final TimerTask timerTask = new TimerTask() {
                /**
                 * Core simulator function.
                 * Called every one second when simulation is running.
                 * Handle correct time management.
                 * Controls behaviour of simulation.
                 */
                @Override
                public void run() {
                    if (!simulationTask) {
                        simulationTask = true;
                        gui.showTime(simulationTime);
                        if (previousRealTime == null) {
                            simulationRefresh();
                            previousRealTime = LocalTime.now();
                            simulationTask = false;
                            simulationHandle();
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
     * Set simulation tick speed in ms.
     * Implicitly restarts simulation.
     *
     * @param simulationSpeed
     */
    public void setSimulationSpeed(double simulationSpeed) throws Exception {
        if (this.simulationState) {
            stop();
            this.simulationSpeed = (int) Math.round(1000 * simulationSpeed);
            start(this.simulationTime);
        } else {
            this.simulationSpeed = (int) Math.round(1000 * simulationSpeed);
        }
    }


    /**
     * Set real time of GUI update in seconds.
     *
     * @param simulationRefreshSpeed
     * @throws Exception
     */
    public void setSimulationRefreshSpeed(int simulationRefreshSpeed) throws Exception {
        if (simulationRefreshSpeed < 1) {
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
        if (this.simulationState) {
            this.simulationState = false;
            if (this.timer != null) {
                this.previousRealTime = null;
                this.timer.cancel();
                this.timer.purge();
                System.err.println("Simulation stopped.");
            }
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
