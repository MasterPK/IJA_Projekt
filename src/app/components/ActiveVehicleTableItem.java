package app.components;

/**
 * TableView item.
 * @author Petr Křehlík, Martin Klobušický
 * @date 13.5.2020
 */
public class ActiveVehicleTableItem {
    /**
     * Line Id.
     */
    private String routeId;
    /**
     * Trip Id.
     */
    private String tripId;

    /**
     * Initialize object.
     * @param routeId {@link ActiveVehicleTableItem#routeId}
     * @param tripId {@link ActiveVehicleTableItem#tripId}
     */
    public ActiveVehicleTableItem(String routeId, String tripId)
    {
        this.routeId=routeId;
        this.tripId=tripId;
    }

    /**
     * @return {@link ActiveVehicleTableItem#routeId}
     */
    public String getRouteId() {
        return routeId;
    }

    /**
     * @return {@link ActiveVehicleTableItem#tripId}
     */
    public String getTripId() {
        return tripId;
    }
}
