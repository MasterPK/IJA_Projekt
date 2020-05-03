package app.models;

import app.model.maps.myMaps.*;
import javafx.application.Platform;

import java.sql.Time;
import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.time.temporal.TemporalAmount;
import java.time.temporal.TemporalUnit;
import java.util.*;

public class Simulator {
    private Timer timer;
    private Calendar calendar;
    private StreetMap streetMap;

    private List<Line> lines = new ArrayList<>();

    private void createExampleLine()
    {
        Line tmp = new MyLine("1");
        tmp.addStop(this.streetMap.getStreet("Koželužská").getStop("Za Rybníkem"));
        tmp.addStop(this.streetMap.getStreet("Koželužská").getStops().get(1));
        tmp.addStreet(this.streetMap.getStreet("Řípovská"));
        tmp.addStop(this.streetMap.getStreet("Revoluční").getStops().get(0));
        lines.add(tmp);

    }

    public Simulator(StreetMap streetMap, Date startTime) {
        this.streetMap=streetMap;
        createExampleLine();
    }

    private boolean simulationState = false;

    private void handleBus(Bus bus)
    {

    }

    private void handleLine(Line line)
    {
        for(Bus bus:line.getLineConnections())
        {
            handleBus(bus);
        }
    }

    public void start()
    {
        final int[] i = {1};
        final TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                LocalTime time = LocalTime.now();
                Platform.runLater(() ->{

                });
                for(Line line:lines)
                {
                    handleLine(line);
                }
            }
        };

        this.timer=new Timer("Simulator");
        this.calendar=Calendar.getInstance();



        System.out.println(lines.get(0).toString());
        Platform.runLater(() -> {
            if(!simulationState)
            {
                timer.schedule(timerTask,0,1000);
                this.simulationState=true;
                System.out.println("Simulation started...");
            }else {
                System.out.println("Simulation already running");
            }

        });
    }

}
