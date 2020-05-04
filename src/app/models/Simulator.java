package app.models;

import app.model.maps.myMaps.Line;
import app.model.maps.myMaps.MyLine;
import app.model.maps.myMaps.StreetMap;
import app.model.maps.myMaps.Trip;
import com.opencsv.CSVReader;
import com.sun.media.sound.InvalidFormatException;
import javafx.application.Platform;

import java.io.FileReader;
import java.io.Reader;
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

    private void createExampleLine() {
        /*Line tmp = new MyLine("1", "Jednička");
        tmp.addStop(this.streetMap.getStreet("Koželužská").getStop("Za Rybníkem"));
        tmp.addStop(this.streetMap.getStreet("Koželužská").getStops().get(1));
        tmp.addStreet(this.streetMap.getStreet("Řípovská"));
        tmp.addStop(this.streetMap.getStreet("Revoluční").getStops().get(0));
        System.err.println(tmp.getStopsLength(this.streetMap.getStreet("Koželužská").getStop("Za Rybníkem"), this.streetMap.getStreet("Revoluční").getStops().get(0)));

        List<LocalTime> timetable = new ArrayList<>();
        timetable.add(LocalTime.parse("12:00:00"));
        timetable.add(LocalTime.parse("12:03:00"));
        timetable.add(LocalTime.parse("12:05:00"));

        tmp.createTrip(1001, timetable);
        lines.add(tmp);*/

    }

    private void loadTimetable() throws Exception {
        loadLines();
        loadTrips();
    }


    public Simulator(StreetMap streetMap, Date startTime, BaseGui gui) throws Exception {
        this.streetMap = streetMap;
        this.gui = gui;

        //createExampleLine();
        loadTimetable();
    }


    private void handleBus(Trip trip) {
        System.out.println("handleBus:" + trip.getId());

        // Is this connection active at current time?
        if (trip.getTimetable().get(0).isBefore(currentTime) || currentTime.isAfter(trip.getTimetable().get(trip.getTimetable().size() - 1))) {
            return;
        }
    }

    private void handleLine(Line line) {
        for (Trip trip : line.getLineConnections()) {
            handleBus(trip);
        }
    }

    private void simulationHandle() {
        refreshTimer++;
        currentTime = LocalTime.now();
        gui.showTime(currentTime);
    }

    private final TimerTask simulationTask = new TimerTask() {

        /**
         * The action to be performed by this timer task.
         */
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

        Platform.runLater(() -> {
            if (!simulationState) {
                this.timer = new Timer("Simulator");
                timer.schedule(this.simulationTask, 0, 1000);
                this.simulationState = true;
                System.err.println("Simulation started...");
            } else {
                System.err.println("Simulation already running");
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
