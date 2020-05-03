package app.model.maps.myMaps;



import java.time.LocalTime;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;

public class MyLine implements Line {
    List<Stop> stops;
    List<Street> streets;
    private String id;
    private List<Trip> trips = new ArrayList<>();

    public List<Trip> getLineConnections() {
        return trips;
    }

    public String getId() {
        return id;
    }

    public MyLine(String id) {
        this.id = id;
        this.stops = new ArrayList<>();
        this.streets = new ArrayList<>();
    }

    public double GetStopsLenght(Stop stop1, Stop stop2){
        double lenght = 0;
        List<Street> lineStreets = new ArrayList<>();
        int first = 0;
        int last = 0;
        if (this.stops.contains(stop1) && this.stops.contains(stop2)){
            if (this.streets.contains(stop1.getStreet()) && this.streets.contains(stop2.getStreet())){
                first = this.streets.indexOf(stop1.getStreet());
                last = this.streets.indexOf(stop2.getStreet());
                for (;first <= last ; first++){
                    lineStreets.add(this.streets.get(first));
                }
                for (int i = 0; i < lineStreets.size();i++){
                    if ( i == 0){
                        Coordinate endCoord = followPoint(lineStreets.get(i),lineStreets.get(i+1));
                        Coordinate stopCoord = stop1.getCoordinate();
                        if (changeX(lineStreets.get(i))){
                            lenght += Math.abs(stopCoord.getX()-endCoord.getX());
                        }
                        else {
                            lenght += Math.abs(stopCoord.getY()-endCoord.getY());
                        }
                    }
                    else if (i == lineStreets.size()-1){
                        Coordinate endCoord2 = followPoint(lineStreets.get(i-1),lineStreets.get(i));
                        Coordinate stopCoord2 = stop2.getCoordinate();

                        if (changeX(lineStreets.get(i))){
                            lenght += Math.abs(stopCoord2.getX()-endCoord2.getX());
                        }
                        else {
                            lenght += Math.abs(stopCoord2.getY()-endCoord2.getY());
                        }
                    }
                    else{
                        Coordinate start11 = lineStreets.get(i).getCoordinates().get(0);
                        Coordinate end11 = lineStreets.get(i).getCoordinates().get(1);

                        if (changeX(lineStreets.get(i))){
                            lenght += Math.abs(start11.getX()-end11.getX());
                        }
                        else{
                            lenght += Math.abs(start11.getY()-end11.getY());
                        }
                    }


                }
            }
        }
        return lenght;
    }

    public Coordinate followPoint(Street street1, Street street2){
        Coordinate start = street1.getCoordinates().get(0);
        Coordinate end = street1.getCoordinates().get(1);

        Coordinate start2 = street2.getCoordinates().get(0);
        Coordinate end2 = street2.getCoordinates().get(1);

        if (start == start2){
            return start;
        }
        if (start == end2){
            return start;
        }
        if (end == start2){
            return end;
        }
        if (end == end2){
            return end;
        }
        return null;
    }

    public boolean changeX(Street street){
        boolean zmenaX;
        Coordinate first = street.getCoordinates().get(0);
        Coordinate second = street.getCoordinates().get(1);

        if (first.getX() != second.getX()){
            zmenaX = true;
        }
        else {
            zmenaX = false;
        }
        return zmenaX;
    }

    @Override
    public boolean addStop(Stop stop) {
        if (this.stops.isEmpty()) {
            this.stops.add(stop);
            this.streets.add(stop.getStreet());
            return true;
        }

        if (this.streets.get(this.streets.size() - 1).follows(stop.getStreet())) {
            this.stops.add(stop);
            this.streets.add(stop.getStreet());
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
                result += street.getId() + ":"+stop+";";
            }
        }
        return result;
    }

    @Override
    public boolean createConnection(Integer id, List<LocalTime> timetable)
    {
        if(id==null || timetable==null)
        {
            return false;
        }

        Trip trip = new Trip(id,timetable);
        trips.add(trip);
        return true;
    }
}
