/*
 * Zdrojové kódy josu součástí zadání 1. úkolu pro předmětu IJA v ak. roce 2019/2020.
 * (C) Radek Kočí
 */
package app.model.maps.myMaps;



import java.util.List;

/**
 * Reprezentuje jednu ulici v mapě. Ulice má svůj identifikátor (název) a je definována souřadnicemi. Pro 1. úkol
 * předpokládejte pouze souřadnice začátku a konce ulice.
 * Na ulici se mohou nacházet zastávky.
 *
 * @author koci
 */
public interface Street {
    /**
     * Vrátí identifikátor ulice.
     *
     * @return Identifikátor ulice.
     */
    public String getId();

    /**
     * Vrátí seznam souřadnic definujících ulici. První v seznamu je vždy počátek a poslední v seznamu konec ulice.
     *
     * @return Seznam souřadnic ulice.
     */

    public List<Coordinate> getCoordinates();

    /**
     * Vrátí seznam zastávek na ulici.
     *
     * @return Seznam zastávek na ulici. Pokud ulize nemá žádnou zastávku, je seznam prázdný.
     */
    public List<Stop> getStops();

    /**
     * Přidá do seznamu zastávek novou zastávku.
     *
     * @param stop Nově přidávaná zastávka.
     */
    boolean addStop(Stop stop);

    static Street defaultStreet(String id, Coordinate... coordinates) {
        if (coordinates.length <= 1)
            return null;

        for (int i = 0; i < coordinates.length-2; i++) {
            int diffX1 = coordinates[i].diffX(coordinates[i+1]);
            int diffY1 = coordinates[i].diffY(coordinates[i+1]);
            int diffX2 = coordinates[i+1].diffX(coordinates[i+2]);
            int diffY2 = coordinates[i+1].diffY(coordinates[i+2]);

            int res = diffX1 * diffX2 + diffY1 * diffY2;

            if (res != 0) {
                return null;
            }
        }
        return new MyStreet(id, coordinates);
    }

    static Street create(String id, List<Coordinate> coordinates) {
        if (coordinates.size() <= 1)
            return null;

        for (int i = 0; i < coordinates.size()-2; i++) {
            int diffX1 = coordinates.get(i).diffX(coordinates.get(i + 1));
            int diffY1 = coordinates.get(i).diffY(coordinates.get(i + 1));
            int diffX2 = coordinates.get(i + 1).diffX(coordinates.get(i + 2));
            int diffY2 = coordinates.get(i + 1).diffY(coordinates.get(i + 2));

            int res = diffX1 * diffX2 + diffY1 * diffY2;

            if (res != 0) {
                return null;
            }
        }
        return new MyStreet(id, coordinates);
    }

    Coordinate begin();
    Coordinate end();
    boolean follows(Street s);
    public Stop getStop(String id);
}
