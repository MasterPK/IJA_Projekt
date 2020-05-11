package app.models.maps;


import app.models.Cloneable;

import java.util.List;

public interface Line extends Cloneable {
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
    boolean isFollowing(Street street, Street street2);

    /**
     * Get real stops count.
     * @return
     */
    int getRealStopsCount();

    List<Stop> getRealStops();
    List<Street> getStreets();
    double getStopAndCoordinateLength(Stop stop1, Stop stop2);
    double getLenghtOfStreet(Street street);

    /**
     * Reset actual timetable to original for every trips in line.
     */
    void resetTimetable();


    /**
     * Add conflict street to list.
     * @param street
     */
    void addConflictStreet(Street street);

    /**
     * Clear conflicts streets list.
     */
    void clearConflicts();


    /**
     * Move streets that follows in map to same object.
     */
    void compressConflicts();

    /**
     * Get count of unique conflicts.
     * @return
     */
    int getConflictsCount();

    /**
     * Get true if there is some conflict, otherwise false.
     * @return
     */
    boolean isConflict();

    /**
     * Get all conflicts.
     * @return
     */
    List<List<Street>> getConflicts();


}

