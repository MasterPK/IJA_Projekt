package app.components;

public class LineTableItem {
    private String routeId;
    private String conflict;

    public LineTableItem(String routeId, String conflict)
    {
        this.routeId=routeId;
        this.conflict=conflict;
    }

    public String getRouteId() {
        return routeId;
    }

    public String getConflict() {
        return conflict;
    }
}
