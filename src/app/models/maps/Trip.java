package app.models.maps;

import javafx.event.EventHandler;
import javafx.scene.input.MouseEvent;
import javafx.scene.shape.Circle;

import java.time.LocalTime;
import java.time.temporal.ChronoField;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;


/**
 * Class for basic work with Trips
 * @author Petr Křehlík, Martin Klobušický
 * @date 13.5.2020
 */
public class Trip {
    private String id;
    private List<LocalTime> plannedTimetable = new ArrayList<>();
    private List<LocalTime> actualTimetable = new ArrayList<>();
    private List<LocalTime> backUpTimetable = new ArrayList<>();
    private Line line;
    private Circle circle;

    /**
     * Get ID of this trip
     * @return ID of trip as string
     */
    public String getId() {
        return id;
    }

    /**
     * Set ID of this trip
     * @param id ID that we want to set for this trip
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * Constructor of trip
     * @param id ID of trip
     * @param timetable timetable of trip
     */
    public Trip(String id, List<LocalTime> timetable) {
        this.id = id;
        this.plannedTimetable=timetable;
        this.actualTimetable.addAll(timetable);
        this.backUpTimetable.addAll(timetable);
    }

    /**
     * Reset timetable of actual trip
     */
    public void resetTimetable()
    {
        this.actualTimetable=new ArrayList<>();
        this.actualTimetable.addAll(this.plannedTimetable);
    }

    /**
     * Restore the original timetable of trip
     */
    public void restoreBackUp()
    {
        this.plannedTimetable.clear();
        this.actualTimetable.clear();
        this.plannedTimetable.addAll(this.backUpTimetable);
        this.actualTimetable.addAll(this.backUpTimetable);
    }

    /**
     * Load backup timetables
     */
    public void loadBackUpTimetable()
    {
        this.actualTimetable=new ArrayList<>(this.backUpTimetable);
        this.plannedTimetable=new ArrayList<>(this.backUpTimetable);
    }

    /**
     *
     * @return
     */
    public List<LocalTime> getBackUpTimetable() {
        return backUpTimetable;
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
