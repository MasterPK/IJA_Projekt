package app.model.maps.myMaps;



import java.time.LocalTime;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;

public class MyLine implements Line {
    List<Stop> stops;
    List<Street> streets;
    private String id;
    private List<Connection> connections = new ArrayList<>();

    public List<Connection> getLineConnections() {
        return connections;
    }

    public String getId() {
        return id;
    }

    public MyLine(String id) {
        this.id = id;
        this.stops = new ArrayList<>();
        this.streets = new ArrayList<>();
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

        Connection connection = new Connection(id,timetable);
        connections.add(connection);
        return true;
    }
}
