package app.components;

import app.models.maps.Line;

/**
 * TableView item.
 * @author Petr Křehlík, Martin Klobušický
 * @date 13.5.2020
 */
public class LineTableItem {
    /**
     * Line Id.
     */
    private String routeId;
    /**
     * Count of conflicts.
     */
    private String conflict;
    /**
     * Line object.
     */
    private Line line;

    /**
     * Initialize object.
     * @param routeId {@link LineTableItem#routeId}
     * @param conflict {@link LineTableItem#conflict}
     * @param line {@link LineTableItem#line}
     */
    public LineTableItem(String routeId, String conflict, Line line)
    {
        this.routeId=routeId;
        this.conflict=conflict;
        this.line=line;
    }

    /**
     *
     * @return {@link LineTableItem#routeId}
     */
    public String getRouteId() {
        return routeId;
    }

    /**
     *
     * @return {@link LineTableItem#conflict}
     */
    public String getConflict() {
        return conflict;
    }

    /**
     *
     * @return {@link LineTableItem#line}
     */
    public Line getLine() {
        return line;
    }
}
