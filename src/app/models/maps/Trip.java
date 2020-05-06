package app.models.maps;

import javafx.event.EventHandler;
import javafx.scene.input.MouseEvent;
import javafx.scene.shape.Circle;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class Trip {
    private String id;
    private List<LocalTime> timetable = new ArrayList<>();
    private Line line;
    private Circle circle;

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

    public void addTimetableItem(String time)
    {
        LocalTime localTime = LocalTime.parse(time);
        timetable.add(localTime);
    }

    public void setCircle(Circle circle) {
        this.circle = circle;
    }


    public Circle getCircle() {
        return circle;
    }

    public void setLine(Line line) {
        this.line = line;
    }

    public Line getLine() {
        return line;
    }
}
