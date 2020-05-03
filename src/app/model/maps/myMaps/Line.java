package app.model.maps.myMaps;


import java.util.List;

public interface Line {
    boolean addStop(Stop stop);

    boolean addStreet(Street street);

    java.util.List<java.util.AbstractMap.SimpleImmutableEntry<Street, Stop>> getRoute();

    String getId();

    static Line defaultLine(String id) {
        return new MyLine(id);
    }

    List<Bus> getLineConnections();
}

