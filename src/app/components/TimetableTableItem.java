package app.components;

public class TimetableTableItem {
    private String stopId;
    private String plannedTime;
    private String actualTime;


    public TimetableTableItem(String stopId,String plannedTime, String actualTime)
    {
        this.stopId=stopId;
        this.plannedTime=plannedTime;
        this.actualTime=actualTime;
    }

    public String getActualTime() {
        return actualTime;
    }

    public String getPlannedTime() {
        return plannedTime;
    }

    public String getStopId() {
        return stopId;
    }
}
