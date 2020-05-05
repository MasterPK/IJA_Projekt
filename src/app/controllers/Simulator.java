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
                                            break;
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

    /**
     *
     * @param currentTime
     * @param startTimePos
     * @param endTimePos
     * @param startStop
     * @param endStop
     * @param line
     * @return Coord of a point where the actual bus is
     */
    public Coordinate dotPosition(LocalTime currentTime, LocalTime startTimePos, LocalTime endTimePos, Stop startStop, Stop endStop, Line line){

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

        for(int i = 0; i< Streets.size()-1;i++){
            if (lenghtPassed == 0){
                break;
            }
           follow = line.followPoint(Streets.get(i),Streets.get(i+1)); // bod konca ulice na ktorej sa bus nachádza

           if (line.changeX(Streets.get(i))){ // kontrola či sa hýbeme po X ose
               if ((Math.abs(follow.getX()-finalCoord.getX()))<= lenghtPassed){ //kontrola či sa bod nachádza na aktuálnej ulici
                   if (line.plusX(finalCoord,follow)){ //kontrola smeru po X ose
                       finalCoord.setX(finalCoord.getX()+(Math.abs(follow.getX()-finalCoord.getX())));
                   }
                   else{
                       finalCoord.setX(finalCoord.getX()-(Math.abs(follow.getX()-finalCoord.getX())));
                   }
                   lenghtPassed -= ((Math.abs(follow.getX()-finalCoord.getX())));
               }
               else{
                   if (line.plusX(finalCoord,follow)){ //kontrola smeru po X ose
                       finalCoord.setX(finalCoord.getX()+lenghtPassedInt);
                   }
                   else{
                       finalCoord.setX(finalCoord.getX()-lenghtPassedInt);
                   }
                   lenghtPassed = 0;
               }
           }
           else
           {
               if ((Math.abs(follow.getY()-finalCoord.getY()))<= lenghtPassed){ //kontrola či sa bod nachádza na aktuálnej ulici
                   if (line.plusY(finalCoord,follow)){ //kontrola smeru po Y ose
                       finalCoord.setY(finalCoord.getY()+(Math.abs(follow.getY()-finalCoord.getY())));
                   }
                   else{
                       finalCoord.setY(finalCoord.getY()-(Math.abs(follow.getY()-finalCoord.getY())));
                   }
                   lenghtPassed -= ((Math.abs(follow.getY()-finalCoord.getY())));
               }
               else{
                   if (line.plusY(finalCoord,follow)){ //kontrola smeru po Y ose
                       finalCoord.setY(finalCoord.getY()+lenghtPassedInt);
                   }
                   else{
                       finalCoord.setY(finalCoord.getY()-lenghtPassedInt);
                   }
                   lenghtPassed = 0;
               }
           }
        }
        return finalCoord;

    }


    public Simulator(StreetMap streetMap, BaseGui gui) throws Exception {
        this.streetMap = streetMap;
        this.gui = gui;

        loadLines();
        loadTrips();
        loadTimetable();
    }


    private void handleTrip(Trip trip,Line line) {
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

        Coordinate tmp = dotPosition(this.simulationTime,trip.getTimetable().get(0),trip.getTimetable().get(1),line.getStops().get(0),line.getStops().get(2),line);

        this.gui.createDot(tmp);

    }

    private void handleLine(Line line) {
        for (Trip trip : line.getLineConnections()) {
            handleTrip(trip,line);
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
        Platform.runLater(() -> {
            if (!simulationState) {
                this.refreshTimer=9;
                final TimerTask timerTask = new TimerTask() {
                    @Override
                    public void run() {
                        simulationHandle();
                        if (refreshTimer == 10) {
                            simulationRefresh();
                        }
                    }
                };

                this.simulationTime=time;
                this.timer = new Timer("Simulator");
                timer.schedule(timerTask, 0, this.simulationSpeed);
                this.simulationState = true;
                System.err.println("Simulation started.");
            } else {
                System.err.println("Simulation already running...");
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
        start(this.simulationTime);
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
