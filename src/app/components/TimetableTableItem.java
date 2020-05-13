package app.components;

/**
 * TableView item.
 * @author Petr Křehlík, Martin Klobušický
 * @date 13.5.2020
 */
public class TimetableTableItem {
    /**
     * Stop Id.
     */
    private String stopId;
    /**
     * Planned time.
     */
    private String plannedTime;
    /**
     * Actual time.
     */
    private String actualTime;


    /**
     * Initialize object.
     * @param stopId {@link TimetableTableItem#stopId}
     * @param plannedTime {@link TimetableTableItem#plannedTime}
     * @param actualTime {@link TimetableTableItem#actualTime}
     */
    public TimetableTableItem(String stopId,String plannedTime, String actualTime)
    {
        this.stopId=stopId;
        this.plannedTime=plannedTime;
        this.actualTime=actualTime;
    }

    /**
     *
     * @return {@link TimetableTableItem#stopId}
     */
    public String getActualTime() {
        return actualTime;
    }

    /**
     *
     * @return {@link TimetableTableItem#plannedTime}
     */
    public String getPlannedTime() {
        return plannedTime;
    }

    /**
     *
     * @return {@link TimetableTableItem#actualTime}
     */
    public String getStopId() {
        return stopId;
    }
}
