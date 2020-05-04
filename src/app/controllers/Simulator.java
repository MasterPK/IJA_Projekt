package app.controllers;

import app.models.maps.*;
import app.view.BaseGui;
import app.models.CSVLoader;
import javafx.application.Platform;

import java.time.LocalTime;
import java.util.*;

import static java.time.temporal.ChronoUnit.MILLIS;

public class Simulator {

    // Simulation
    private Timer timer;
    private double refreshTimer = 0;
    private boolean simulationState = false;
    private LocalTime simulationTime;
    private int simulationSpeed = 1000; //ms

    // Model
    private StreetMap streetMap;
    private List<Line> lines = new ArrayList<>();

    // View
    private BaseGui gui;


    private void loadLines() throws Exception {
        List<String[]> list = CSVLoader.load("data/routes.txt", new String[]{"route_id", "route_short_name"});

        for (String[] string : list) {
            String routeId = string[0];
            String routeShortTime = string[1];
            Line route = new MyLine(routeId, routeShortTime);
            this.lines.add(route);
        }
    }

    private void loadTrips() throws Exception {

        List<String[]> list = CSVLoader.load("data/trips.txt", new String[]{"route_id", "trip_id"});

        for (String[] string : list) {
            String routeId = string[0];
            String tripId = string[1];

            for (Line line : this.lines) {
                if (!routeId.equals(line.getId())) {
                    throw new Exception("Route " + routeId + " doesnt exist!");
                }
                Trip trip = new Trip(tripId);
                line.addTrip(trip);
            }
        }
    }

    private void loadTimetable() throws Exception {
        List<String[]> list = CSVLoader.load("data/stop_times.txt", new String[]{"trip_id", "time", "stop_id", "street_id"});

        for (String[] string : list) {
            String tripId = string[0];
            String time = string[1];
            String stopId = string[2];
            String streetId = string[3];
            for (Line line : this.lines) {
                for (Trip trip : line.getTrips()) {
                    if (trip.getId().equals(tripId)) {
                        for (Street street : streetMap.getStreets()) {
                            if (streetId.equals(street.getId())) {
                                if (stopId.isEmpty()) {
                                    if(time.isEmpty())
                                    {
                                        line.addStreet(street);
                                    }
                                } else {
                                    for (Stop stop : street.getStops()) {
                                        if (stop.getId().equals(stopId)) {
                                            line.addStop(stop);
                                            trip.addTimetableItem(time);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }


    public Coordinate DotPosition(LocalTime currentTime, LocalTime startTimePos, LocalTime endTimePos, Stop startStop, Stop endStop, Line line){

        Coordinate finalCoord = startStop.getCoordinate();
        Coordinate follow;
        List<Street> Streets = new ArrayList<>();

        LocalTime tripTimeTotal = endTimePos.minusHours(startTimePos.getHour())
                .minusMinutes(startTimePos.getMinute())
                .minusSeconds(startTimePos.getSecond());

        LocalTime tripTimeActual = currentTime.minusHours(startTimePos.getHour())
                .minusMinutes(startTimePos.getMinute())
                .minusSeconds(startTimePos.getSecond());

        int actualSeconds = (tripTimeActual.getHour()*60*60)+(tripTimeActual.getMinute()*60)+(tripTimeActual.getSecond());
        int totalSeconds = (tripTimeTotal.getHour()*60*60)+(tripTimeTotal.getMinute()*60)+(tripTimeTotal.getSecond());

        float actualPercent = (actualSeconds * 100.0f) / totalSeconds;

        double lineLenght = line.getStopsLength(startStop,endStop);

        double lenghtPassed = (actualPercent/100) * lineLenght;

        int lenghtPassedInt = (int)lenghtPassed;


        Streets = line.getStreetsBetween(startStop,endStop);

        for(int i = 0; i< Streets.size();i++){
            if (lenghtPassed == 0){
                break;
            }
           follow = line.followPoint(Streets.get(i),Streets.get(i+1));
           if ( i == 0){
               if (line.changeX(startStop.getStreet())){
                   if ((Math.abs(follow.getX()-startStop.getCoordinate().getX()))<= lenghtPassed){
                       finalCoord.setX(Math.abs(follow.getX()-startStop.getCoordinate().getX()));
                       lenghtPassed -= ((Math.abs(follow.getX()-startStop.getCoordinate().getX())));
                   }
                   else{
                       finalCoord.setX(Math.abs(lenghtPassedInt-startStop.getCoordinate().getX()));
                       lenghtPassed = 0;
                   }

               }
           }
        }
        return finalCoord;

    }


    public Simulator(StreetMap streetMap, LocalTime startTime, BaseGui gui) throws Exception {
        this.streetMap = streetMap;
        this.gui = gui;
        this.simulationTime = startTime;

        loadLines();
        loadTrips();
        loadTimetable();
    }


    private void handleTrip(Trip trip) {
        System.out.println("handleBus:" + trip.getId());

        if (trip.getTimetable().isEmpty()) {
            return;
        }
        // Is this connection active at current time?
        if (trip.getTimetable().get(0).isBefore(simulationTime) || simulationTime.isAfter(trip.getTimetable().get(trip.getTimetable().size() - 1))) {
            return;
        }
    }

    private void handleLine(Line line) {
        for (Trip trip : line.getLineConnections()) {
            handleTrip(trip);
        }
    }

    private void simulationRefresh() {
        refreshTimer = 0;
        gui.clearSimulationGui();

        System.out.println("Refresh simulation...");
        for (Line line : lines) {
            handleLine(line);
        }
    }

    private void simulationHandle() {
        refreshTimer++;
        simulationTime = simulationTime.plus(this.simulationSpeed, MILLIS);
        gui.showTime(simulationTime);
    }

    final TimerTask timerTask = new TimerTask() {
        @Override
        public void run() {
            simulationHandle();
            if (refreshTimer == 10) {
                simulationRefresh();
            }

        }
    };


    /**
     * Start simulation
     */
    public void start() {

        System.out.println(lines.get(0).toString());
        Platform.runLater(() -> {
            if (!simulationState) {
                this.timer = new Timer("Simulator");
                timer.schedule(timerTask, 0, this.simulationSpeed);
                this.simulationState = true;
                System.out.println("Simulation started...");
            } else {
                System.out.println("Simulation already running");
            }

        });
    }

    /**
     * Set simulation tick speed in ms. Refresh of GUI is every 10 ticks.
     * Implicitly restarts simulation.
     *
     * @param simulationSpeed
     */
    public void setSimulationSpeed(int simulationSpeed) {
        this.simulationSpeed = simulationSpeed;
        stop();
        start();
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
        this.timer.cancel();
        this.timer.purge();
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
