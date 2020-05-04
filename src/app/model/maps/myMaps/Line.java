package app.model.maps.myMaps;


import java.time.LocalTime;
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
}

