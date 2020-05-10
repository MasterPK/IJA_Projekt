package app.components;

import app.models.maps.Line;

public class LineTableItem {
    private String routeId;
    private String conflict;
    private Line line;

    public LineTableItem(String routeId, String conflict, Line line)
    {
        this.routeId=routeId;
        this.conflict=conflict;
        this.line=line;
    }

    public String getRouteId() {
        return routeId;
    }

    public String getConflict() {
        return conflict;
    }

    public Line getLine() {
        return line;
    }
}
