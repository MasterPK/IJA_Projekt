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

    private void createExampleLine() {
        Line tmp = new MyLine("1");
        tmp.addStop(this.streetMap.getStreet("Koželužská").getStop("Za Rybníkem"));
        tmp.addStop(this.streetMap.getStreet("Koželužská").getStops().get(1));
        tmp.addStreet(this.streetMap.getStreet("Řípovská"));
        tmp.addStop(this.streetMap.getStreet("Revoluční").getStops().get(0));
        System.out.println( tmp.getStopsLength(this.streetMap.getStreet("Koželužská").getStop("Za Rybníkem"),this.streetMap.getStreet("Revoluční").getStops().get(0)));

        List<LocalTime> timetable = new ArrayList<>();
        timetable.add(LocalTime.parse("12:00:00"));
        timetable.add(LocalTime.parse("12:03:00"));
        timetable.add(LocalTime.parse("12:05:00"));
        tmp.createTrip(1001,timetable);
        lines.add(tmp);




    }



    public Simulator(StreetMap streetMap, Date startTime, BaseGui gui) {
        this.streetMap = streetMap;
        createExampleLine();
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

    public void start() {


        final int[] i = {1};
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

        this.timer = new Timer("Simulator");

        System.out.println(lines.get(0).toString());
        Platform.runLater(() -> {
            if (!simulationState) {
                timer.schedule(timerTask, 0, 1000);
                this.simulationState = true;
                System.out.println("Simulation started...");
            } else {
                System.out.println("Simulation already running");
            }

        });
    }

}
