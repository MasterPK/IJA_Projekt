package app.models.maps;


import java.util.List;

public interface Line extends Cloneable {

    /**
     * Add stop to list of stops on line and add street of stop that we are adding
     * @param stop stop that we want to add to line
     * @return true if it was successful
     */
    boolean addStop(Stop stop);

    /**
     * Add street to line
     * @param street
     * @return true if it was successful
     */
    boolean addStreet(Street street);

    /**
     * Get list of Streets and Stops on line
     * @return list of Stops on line and List of Streets on line
     */
    java.util.List<java.util.AbstractMap.SimpleImmutableEntry<Street, Stop>> getRoute();

    /**
     * Get id of line
     * @return id of line
     */
    String getId();

    /**
     * Get all trips on actual line
     * @return trips on this line
     */
    List<Trip> getLineConnections();
    /**
     * Get trips of line
     * @return list of Trips in line
     */
    List<Trip> getTrips();

    /**
     * Add trip on line
     * @param trip trip that we want to add
     */
    void addTrip(Trip trip);
    /**
     * Compute lenght between 2 bus stops.
     * @param stop1 starting stop
     * @param stop2 ending stop
     * @return actual lenght between bus stops as double value.
     */
    double getStopsLength(Stop stop1, Stop stop2);
    /**
     * Get streets between 2 bus stops.
     * @param stop1 starting stop
     * @param stop2 ending stop
     * @return List of streets between stops.
     */
    List<Street> getStreetsBetween(Stop stop1, Stop stop2);
    /**
     * Return a Coordinate that joins 2 streets.
     * @param street1 first street
     * @param street2 second street
     * @return Coordinate that is identical for both streets.
     */
    Coordinate followPoint(Street street1, Street street2);
    /**
     * Compute if street is horizontal or vertical.
     * @param street computed street
     * @return return true if street if horizontal and false if vertical.
     */
    boolean changeX(Street street);
    /**
     * Finding what coord point is more higher on the map.
     * @param coord1 first coord
     * @param coord2 second coord
     * @return Boolean value if first coord is more higher or no.
     */
    boolean plusY(Coordinate coord1, Coordinate coord2);
    /**
     * Finding what coord point is more left on the map.
     * @param coord1 first coordinate
     * @param coord2 second coordinate
     * @return Boolean value if first coord is more left or no.
     */
    boolean plusX(Coordinate coord1, Coordinate coord2);
    /**
     * Get all stops on line
     * @return return stops on line
     */
    List<Stop> getStops();
    /**
     * Get stop by real index, ignore streets with no stop
     *
     * @param index what index I want
     * @return Stop on index.
     */
    Stop getStopByIndex(int index);
    /**
     * Function that will find if streets bind to each other
     *
     * @param street first street
     * @param street2 second street
     * @return boolean value if second street follow first street
     */
    boolean isFollowing(Street street, Street street2);

    /**
     * Get real stops count.
     *
     * @return count of stops on line
     */
    int getRealStopsCount();

    /**
     * Get real stops
     * @return Gest list of stops with no null values
     */
    List<Stop> getRealStops();
    /**
     * Return streets on line.
     * @return Streets on line
     */
    List<Street> getStreets();
    /**
     * backup function
     * @param stop1 starting stop
     * @param stop2 ending stop
     * @return Lenght between imaginary stops
     */
    double getStopAndCoordinateLength(Stop stop1, Stop stop2);
    /**
     * Compute lenght of street.
     * @param street street that I want to compute.
     * @return lenght of street as double value.
     */
    double getLenghtOfStreet(Street street);

    /**
     * Reset actual timetable to original for every trips in line.
     */
    void resetTimetable();


    /**
     * Add conflict street to list.
     * @param street street that we want to add
     */
    void addConflictStreet(Street street);

    /**
     * Clear conflicts streets list.
     */
    //void clearConflicts();


    /**
     * Move streets that follows in map to same object.
     */
    //void compressConflicts();

    /**
     * Get count of unique conflicts.
     * @return number of conflicts
     */
    int getConflictsCount();

    /**
     * Get true if there is some conflict, otherwise false.
     * @return true if there is a conflict
     */
    boolean isConflict();

    /**
     * Get all conflicts.
     * @return return all conflicts
     */
    List<List<Street>> getConflicts();

    /**
     * Clone object
     * @return clone of this object
     * @throws CloneNotSupportedException error
     */
    Object clone() throws CloneNotSupportedException;


    /**
     * BackUp private lists.
     */
    void backUp();


    /**
     * Restore original lists.
     */
    void restoreBackUp();

    /**
     * Compute conflicts.
     * Implicitly restore backup and compress results.
     */
    void computeConflicts();


}

