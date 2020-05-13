package app.models.maps;


import java.util.ArrayList;
import java.util.List;

/**
 * Class for basic work with Map
 * @author Petr Křehlík, Martin Klobušický
 * @date 13.5.2020
 */
public class MyStreetMap implements StreetMap {

    private List<Street> streets;

    public MyStreetMap() {
        streets = new ArrayList<>();
    }

    /**
     * Přidá ulici do mapy.
     *
     * @param s Objekt reprezentující ulici.
     */
    public void addStreet(Street s) {
        if (s == null)
            return;

        this.streets.add(s);
    }

    /**
     * Vrátí objekt reprezentující ulici se zadaným id.
     *
     * @param id Identifikátor ulice.
     * @return Nalezenou ulici. Pokud ulice s daným identifikátorem není součástí mapy, vrací null.
     */
    public Street getStreet(String id) {
        for (Street street : this.streets) {
            if (street.getId().equals(id))
                return street;
        }
        return null;
    }

    /**
     * Check if street maps are same
     * @param o street map that we want to compare to actual map
     * @return true if they are same
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MyStreetMap that = (MyStreetMap) o;
        return streets.equals(that.streets);
    }

    /**
     * Get streets on actual map.
     * @return List of streets of map.
     */
    public List<Street> getStreets() {
        return streets;
    }

}
