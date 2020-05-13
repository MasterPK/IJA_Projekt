/*
 * Zdrojové kódy josu součástí zadání 1. úkolu pro předmětu IJA v ak. roce 2019/2020.
 * (C) Radek Kočí
 */
package app.models.maps;



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

    /**
     * Set defaul street
     * @param id id of street
     * @param coordinates coordinates of street
     * @return new created street
     */
    static Street defaultStreet(String id, Coordinate... coordinates) {
        if (coordinates.length <= 1)
            return null;

        for (int i = 0; i < coordinates.length-2; i++) {
            double diffX1 = coordinates[i].diffX(coordinates[i+1]);
            double diffY1 = coordinates[i].diffY(coordinates[i+1]);
            double diffX2 = coordinates[i+1].diffX(coordinates[i+2]);
            double diffY2 = coordinates[i+1].diffY(coordinates[i+2]);

            double res = diffX1 * diffX2 + diffY1 * diffY2;

            if (res != 0) {
                return null;
            }
        }
        return new MyStreet(id, coordinates);
    }

    /**
     * Create new street
     * @param id id of street
     * @param coordinates Coordinatesof street
     * @return created street
     */
    static Street create(String id, List<Coordinate> coordinates) {
        if (coordinates.size() <= 1)
            return null;

        for (int i = 0; i < coordinates.size()-2; i++) {
            double diffX1 = coordinates.get(i).diffX(coordinates.get(i + 1));
            double diffY1 = coordinates.get(i).diffY(coordinates.get(i + 1));
            double diffX2 = coordinates.get(i + 1).diffX(coordinates.get(i + 2));
            double diffY2 = coordinates.get(i + 1).diffY(coordinates.get(i + 2));

            double res = diffX1 * diffX2 + diffY1 * diffY2;

            if (res != 0) {
                return null;
            }
        }
        return new MyStreet(id, coordinates);
    }

    /**
     * Starting Coordinate of street
     * @return first coordinate of street
     */
    Coordinate begin();
    /**
     * Get last coord of street
     * @return last coordinate of street
     */
    Coordinate end();
    /**
     * Check if street follows actual street
     * @param s street that should follow actual street
     * @return true if streets are followed
     */
    boolean follows(Street s);
    /**
     * Get stop on actual street
     * @param id id of stop
     * @return Stop that we are looking for
     */
    Stop getStop(String id);
    /**
     * Set cooeficient of trafic on actual street
     * @param trafficCoefficient Coeficient that we want so set
     * @throws Exception
     */
    void setTrafficCoefficient(int trafficCoefficient) throws Exception;
    /**
     * Get the trafic coeficient of actual street
     * @return Trafic coeficient of street
     */
    int getTrafficCoefficient();
    /**
     * Draw street
     * @param gui GIU that we want to set
     */
    void setGui(javafx.scene.shape.Line gui);
    /**
     * Return GIU
     * @return GUI
     */
    javafx.scene.shape.Line getGui();
    /**
     * check if Street is closed
     * @return true if street is closed
     */
    boolean isClosed();
    /**
     * Check if street is open
     * @return true if street is open
     */
    boolean isOpen();
    /**
     * Close or open the street
     * @param closed boolean if we want to close or open street
     */
    void setClosed(boolean closed);
    /**
     * Get lines of street
     * @return lines of street
     */
    List<Line> getLines();
    /**
     * Add line to street
     * @param line line that we want to add
     */
    void addLine(Line line);
}
