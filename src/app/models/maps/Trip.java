package app.models.maps;

import com.sun.org.apache.bcel.internal.generic.LoadClass;
import javafx.event.EventHandler;
import javafx.scene.input.MouseEvent;
import javafx.scene.shape.Circle;

import java.time.LocalTime;
import java.time.temporal.ChronoField;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

public class Trip {
    private String id;
    private List<LocalTime> plannedTimetable = new ArrayList<>();
    private List<LocalTime> actualTimetable = new ArrayList<>();
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
        this.plannedTimetable=timetable;
        this.actualTimetable.addAll(timetable);

    }


    public List<LocalTime> getPlannedTimetable() {
        return plannedTimetable;
    }

    public List<LocalTime> getActualTimetable() {
        return actualTimetable;
    }

    public Trip(String id) {
        this.id = id;
    }

    public List<LocalTime> getTimetable() {
        return actualTimetable;
    }

    public void addTimetableItem(String time)
    {
        LocalTime localTime = LocalTime.parse(time);
        plannedTimetable.add(localTime);
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
