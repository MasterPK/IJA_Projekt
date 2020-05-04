package app.models;

import app.model.maps.myMaps.*;
import javafx.application.Platform;

import java.time.LocalTime;
import java.util.*;

public class Simulator {
    private Timer timer;
    private StreetMap streetMap;
    private double refreshTimer = 0;
    private boolean simulationState = false;
    private List<Line> lines = new ArrayList<>();
    private BaseGui gui;
    private LocalTime currentTime = LocalTime.now();

    private void loadLines() {
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

            for(Line line:this.lines)
            {
                if(!routeId.equals(line.getId()))
                {
                    throw new Exception("Route "+routeId+" doesnt exist!");
                }
                Trip trip = new Trip(tripId);
                line.addTrip(trip);
            }
        }

    }

    public Simulator(StreetMap streetMap, Date startTime, BaseGui gui) throws Exception {
        this.streetMap = streetMap;
        loadLines();
        loadTrips();
        this.gui=gui;
    }


    private void handleBus(Trip trip) {
        System.out.println("handleBus:"+ trip.getId());

        // Is this connection active at current time?
        if(trip.getTimetable().get(0).isBefore(currentTime) || currentTime.isAfter(trip.getTimetable().get(trip.getTimetable().size()-1)))
        {
            return;
        }
    }

    private void handleLine(Line line) {
        for (Trip trip : line.getLineConnections()) {
            handleBus(trip);
        }
    }

    private void simulationHandle()
    {
        refreshTimer++;
        currentTime = LocalTime.now();
        gui.showTime(currentTime);
    }

    final TimerTask timerTask = new TimerTask() {
        @Override
        public void run() {
            simulationHandle();


            if (refreshTimer == 3) {
                refreshTimer = 0;

                gui.clearGui();

                System.out.println("Refresh simulation...");
                for (Line line : lines) {
                    handleLine(line);
                }
            }

        }
    };

    public void start() {

        System.out.println(lines.get(0).toString());
        Platform.runLater(() -> {
            if (!simulationState) {
                this.timer = new Timer("Simulator");
                timer.schedule(timerTask, 0, 1000);
                this.simulationState = true;
                System.out.println("Simulation started...");
            } else {
                System.out.println("Simulation already running");
            }

        });
    }


    public boolean getSimulationState() {
        return this.simulationState;
    }

    public void stop() {
        this.simulationState = false;
        this.timer.cancel();
        this.timer.purge();
    }

}
