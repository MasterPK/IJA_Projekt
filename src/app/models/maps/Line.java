package app.models.maps;


import java.util.List;

public interface Line {
    boolean addStop(Stop stop);

    boolean addStreet(Street street);

    java.util.List<java.util.AbstractMap.SimpleImmutableEntry<Street, Stop>> getRoute();

    String getId();

    List<Trip> getLineConnections();
    List<Trip> getTrips();

    void addTrip(Trip trip);
    double getStopsLength(Stop stop1, Stop stop2);
    List<Street> getStreetsBetween(Stop stop1, Stop stop2);
    Coordinate followPoint(Street street1, Street street2);
    boolean changeX(Street street);
    boolean plusY(Coordinate coord1, Coordinate coord2);
    boolean plusX(Coordinate coord1, Coordinate coord2);
    List<Stop> getStops();
    Stop getStopByIndex(int index);

    /**
     * Get real stops count.
     * @return
     */
    int getRealStopsCount();

}

