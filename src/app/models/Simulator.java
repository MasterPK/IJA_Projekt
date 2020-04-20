package app.models;

import app.model.maps.myMaps.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Timer;

public class Simulator {
    private Timer timer;
    private Date date;
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



    public void start()
    {
        System.out.println(lines.get(0).toString());
    }

}
