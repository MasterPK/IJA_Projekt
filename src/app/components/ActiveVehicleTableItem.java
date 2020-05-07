package app.components;

public class ActiveVehicleTableItem {
    private String routeId;
    private String tripId;

    public ActiveVehicleTableItem(String routeId, String tripId)
    {
        this.routeId=routeId;
        this.tripId=tripId;
    }

    public String getRouteId() {
        return routeId;
    }

    public String getTripId() {
        return tripId;
    }
}
