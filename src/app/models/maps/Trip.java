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
     * Get original timetable.
     * @return Original timetable.
     */
    public List<LocalTime> getBackUpTimetable() {
        return backUpTimetable;
    }

    /**
     * @return Timetable that counts with closed streets.
     */
    public List<LocalTime> getPlannedTimetable() {
        return plannedTimetable;
    }

    /**
     * @return Timetable that counts with closed streets and traffic.
     */
    public List<LocalTime> getActualTimetable() {
        return actualTimetable;
    }

    /**
     * Construct object
     * @param id Unique id.
     */
    public Trip(String id) {
        this.id = id;
    }

    /**
     *
     * @return Timetable that counts with closed streets and traffic.
     */
    public List<LocalTime> getTimetable() {
        return actualTimetable;
    }

    /**
     * Add new item to planned timetable.
     * @param time String time
     */
    public void addTimetableItem(String time)
    {
        LocalTime localTime = LocalTime.parse(time);
        plannedTimetable.add(localTime);
    }

    /**
     * Set gui circle.
     * @param circle Circle element.
     */
    public void setCircle(Circle circle) {
        this.circle = circle;
    }


    /**
     * Get gui element.
     * @return GUI element.
     */
    public Circle getCircle() {
        return circle;
    }

    /**
     * Set line.
     * @param line New line to set.
     */
    public void setLine(Line line) {
        this.line = line;
    }

    /**
     *
     * @return Actual line.
     */
    public Line getLine() {
        return line;
    }
}
