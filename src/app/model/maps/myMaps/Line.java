package app.model.maps.myMaps;


public interface Line {
    boolean addStop(Stop stop);
    boolean addStreet(Street street);
    java.util.List<java.util.AbstractMap.SimpleImmutableEntry<Street,Stop>> getRoute();
    String getId();
    static Line defaultLine(String id) {
        return new MyLine(id);
    }
}
