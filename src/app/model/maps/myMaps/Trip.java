package app.model.maps.myMaps;

import javafx.scene.shape.Circle;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Trip {
    private int id;
    private List<LocalTime> timetable = new ArrayList<>();

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Trip(int id, List<LocalTime> timetable) {
        this.id = id;
        this.timetable=timetable;
    }

    public List<LocalTime> getTimetable() {
        return timetable;
    }
}
