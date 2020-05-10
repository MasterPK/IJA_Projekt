package app.models.maps;


import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class MyLine implements Line {
    List<Stop> stops;
    List<Street> streets;
    private String id;
    private String name;
    private List<Trip> trips = new ArrayList<>();
    private boolean conflict = false;

    public List<Trip> getLineConnections() {
        return trips;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public List<Stop> getStops() {
        return stops;
    }

    public MyLine(String id, String name) {
        this.id = id;
        this.name = name;
        this.stops = new ArrayList<>();
        this.streets = new ArrayList<>();
    }


    public void setConflict(boolean conf){
        this.conflict = conf;
    }
    public boolean getConflict(){
        return this.conflict;
    }


    public double getStopAndCoordinateLength(Stop stop1, Stop stop2) {
        double lenght = 0;
        List<Street> lineStreets = new ArrayList<>();
        int first = 0;
        int last = 0;
                first = this.streets.indexOf(stop1.getStreet());
                last = this.streets.indexOf(stop2.getStreet());
                if (stop1.getStreet().equals(stop2.getStreet())) {
                    if (changeX(stop1.getStreet())) {
                        return Math.abs(stop1.getCoordinate().getX() - stop2.getCoordinate().getX());
                    } else {
                        return Math.abs(stop1.getCoordinate().getY() - stop2.getCoordinate().getY());
                    }
                }
                for (; first <= last; first++) {
                    lineStreets.add(this.streets.get(first));
                }
                for (int i = 0; i < lineStreets.size(); i++) {
                    if (i == 0) {
                        Coordinate endCoord = followPoint(lineStreets.get(i), lineStreets.get(i + 1));
                        Coordinate stopCoord = stop1.getCoordinate();
                        if (changeX(lineStreets.get(i))) {
                            lenght += Math.abs(stopCoord.getX() - endCoord.getX());
                        } else {
                            lenght += Math.abs(stopCoord.getY() - endCoord.getY());
                        }
                    } else if (i == lineStreets.size() - 1) {
                        Coordinate endCoord2 = followPoint(lineStreets.get(i - 1), lineStreets.get(i));
                        Coordinate stopCoord2 = stop2.getCoordinate();

                        if (changeX(lineStreets.get(i))) {
                            lenght += Math.abs(stopCoord2.getX() - endCoord2.getX());
                        } else {
                            lenght += Math.abs(stopCoord2.getY() - endCoord2.getY());
                        }
                    } else {
                        Coordinate start11 = lineStreets.get(i).getCoordinates().get(0);
                        Coordinate end11 = lineStreets.get(i).getCoordinates().get(1);

                        if (changeX(lineStreets.get(i))) {
                            lenght += Math.abs(start11.getX() - end11.getX());
                        } else {
                            lenght += Math.abs(start11.getY() - end11.getY());
                        }
                    }


                }
        return lenght;
    }

    public double getLenghtOfStreet(Street street){
        if (Math.abs(street.getCoordinates().get(0).getX()-street.getCoordinates().get(1).getX())>0){
            return (Math.abs(street.getCoordinates().get(0).getX()-street.getCoordinates().get(1).getX()));
        }
        else {
            return (Math.abs(street.getCoordinates().get(0).getY()-street.getCoordinates().get(1).getY()));
        }
    }

    /**
     * Reset actual timetable to original for every trips in line.
     */
    @Override
    public void resetTimetable() {
        for(Trip trip:this.trips)
        {
            trip.resetTimetable();
        }
    }

    public double getStopsLength(Stop stop1, Stop stop2) {
        double lenght = 0;
        List<Street> lineStreets = new ArrayList<>();
        int first = 0;
        int last = 0;
        if (this.stops.contains(stop1) && this.stops.contains(stop2)) {
            if (this.streets.contains(stop1.getStreet()) && this.streets.contains(stop2.getStreet())) {
                first = this.streets.indexOf(stop1.getStreet());
                last = this.streets.indexOf(stop2.getStreet());
                if (stop1.getStreet().equals(stop2.getStreet())) {
                    if (changeX(stop1.getStreet())) {
                        return Math.abs(stop1.getCoordinate().getX() - stop2.getCoordinate().getX());
                    } else {
                        return Math.abs(stop1.getCoordinate().getY() - stop2.getCoordinate().getY());
                    }
                }
                for (; first <= last; first++) {
                    lineStreets.add(this.streets.get(first));
                }
                for (int i = 0; i < lineStreets.size(); i++) {
                    if (i == 0) {
                        Coordinate endCoord = followPoint(lineStreets.get(i), lineStreets.get(i + 1));
                        Coordinate stopCoord = stop1.getCoordinate();
                        if (changeX(lineStreets.get(i))) {
                            lenght += Math.abs(stopCoord.getX() - endCoord.getX());
                        } else {
                            lenght += Math.abs(stopCoord.getY() - endCoord.getY());
                        }
                    } else if (i == lineStreets.size() - 1) {
                        Coordinate endCoord2 = followPoint(lineStreets.get(i - 1), lineStreets.get(i));
                        Coordinate stopCoord2 = stop2.getCoordinate();

                        if (changeX(lineStreets.get(i))) {
                            lenght += Math.abs(stopCoord2.getX() - endCoord2.getX());
                        } else {
                            lenght += Math.abs(stopCoord2.getY() - endCoord2.getY());
                        }
                    } else {
                        Coordinate start11 = lineStreets.get(i).getCoordinates().get(0);
                        Coordinate end11 = lineStreets.get(i).getCoordinates().get(1);

                        if (changeX(lineStreets.get(i))) {
                            lenght += Math.abs(start11.getX() - end11.getX());
                        } else {
                            lenght += Math.abs(start11.getY() - end11.getY());
                        }
                    }


                }
            }
        }
        return lenght;
    }

    /**
     * Get stop by real index, ignore streets with no stop
     *
     * @param index
     * @return
     */
    public Stop getStopByIndex(int index) {
        int counter = 0;
        for (Stop stop : this.stops) {
            if (stop != null) {
                if (counter == index) {
                    return stop;
                }
                counter++;
            }
        }
        return null;
    }

    /**
     * Get real stops count.
     *
     * @return
     */
    @Override
    public int getRealStopsCount() {
        int counter=0;
        for(Stop stop:this.getStops())
        {
            if(stop!=null)
            {
                counter++;
            }
        }
        return counter;
    }

    @Override
    public List<Stop> getRealStops() {
        List<Stop> stops = new ArrayList<>();
        for(Stop stop:this.getStops())
        {
            if(stop!=null)
            {
                stops.add(stop);
            }
        }
        return stops;
    }

    public List<Street> getStreets() {
        return streets;
    }

    public List<Street> getStreetsBetween(Stop stop1, Stop stop2) {
        List<Street> lineStreets = new ArrayList<>();
        int first = 0;
        int last = 0;

        first = this.streets.indexOf(stop1.getStreet());
        last = this.streets.indexOf(stop2.getStreet());
        if (stop1.getStreet().equals(stop2.getStreet())) {
            lineStreets.add(stop1.getStreet());
            return lineStreets;
        }
        for (; first <= last; first++) {
            lineStreets.add(this.streets.get(first));
        }
        return lineStreets;
    }

    public Coordinate followPoint(Street street1, Street street2) {
        Coordinate start = street1.getCoordinates().get(0);
        Coordinate end = street1.getCoordinates().get(1);

        Coordinate start2 = street2.getCoordinates().get(0);
        Coordinate end2 = street2.getCoordinates().get(1);

        if (start.equals(start2)) {
            return start;
        }
        if (start.equals(end2)) {
            return start;
        }
        if (end.equals(start2)) {
            return end;
        }
        if (end.equals(end2)) {
            return end;
        }
        return null;
    }

    public boolean plusX(Coordinate coord1, Coordinate coord2) {
        boolean plus;
        if (coord1.getX() - coord2.getX() < 0) {
            plus = true;
        } else {
            plus = false;
        }
        return plus;
    }

    public boolean plusY(Coordinate coord1, Coordinate coord2) {
        boolean plus;
        if (coord1.getY() - coord2.getY() < 0) {
            plus = true;
        } else {
            plus = false;
        }
        return plus;
    }

    public boolean changeX(Street street) {
        boolean zmenaX;
        Coordinate first = street.getCoordinates().get(0);
        Coordinate second = street.getCoordinates().get(1);

        if (first.getX() != second.getX()) {
            zmenaX = true;
        } else {
            zmenaX = false;
        }
        return zmenaX;
    }

    @Override
    public boolean addStop(Stop stop) {
        if (this.stops.isEmpty()) {
            this.stops.add(stop);
            if (this.streets.isEmpty()) {
                this.streets.add(stop.getStreet());
            } else {
                if (!this.streets.get(this.streets.size() - 1).getId().equals(stop.getId())) {
                    this.streets.add(stop.getStreet());
                }
            }
            return true;
        }

        if (this.streets.get(this.streets.size() - 1).follows(stop.getStreet())) {
            this.stops.add(stop);
            if (!this.streets.get(this.streets.size() - 1).getId().equals(stop.getStreet().getId())) {
                this.streets.add(stop.getStreet());
            }
            return true;
        }
        return false;

    }

    @Override
    public boolean addStreet(Street street) {
        if (this.stops.isEmpty()) {
            return false;
        }
        if (this.streets.get(this.streets.size() - 1).follows(street)) {
            this.stops.add(null);
            this.streets.add(street);
            return true;
        }
        return false;
    }

    @Override
    public List<AbstractMap.SimpleImmutableEntry<Street, Stop>> getRoute() {
        List<AbstractMap.SimpleImmutableEntry<Street, Stop>> result = new ArrayList<>();
        for (int i = 0; i < this.streets.size(); i++) {
            result.add(new AbstractMap.SimpleImmutableEntry<Street, Stop>(this.streets.get(i), this.stops.get(i)));
        }
        return result;
    }


    @Override
    public String toString() {
        String result = "";
        for (int i = 0; i < this.streets.size(); i++) {
            Street street = this.streets.get(i);
            Stop stop = this.stops.get(i);
            if (stop == null) {
                result += street.getId() + ":null;";
            } else {
                result += street.getId() + ":" + stop + ";";
            }
        }
        return result;
    }

    @Override
    public void addTrip(Trip trip) {
        trips.add(trip);
    }

    public List<Trip> getTrips() {
        return trips;
    }

    public boolean isConflict() {
        return conflict;
    }
}
