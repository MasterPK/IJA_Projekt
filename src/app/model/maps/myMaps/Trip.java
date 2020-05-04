package app.model.maps.myMaps;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class Trip {
    private String id;
    private List<LocalTime> timetable = new ArrayList<>();

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Trip(String id, List<LocalTime> timetable) {
        this.id = id;
        this.timetable=timetable;
    }

    public Trip(String id) {
        this.id = id;
    }

    public List<LocalTime> getTimetable() {
        return timetable;
    }
}
