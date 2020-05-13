package app.models.maps;


import javafx.application.Platform;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Class for basic work with Streets
 * @author Petr Křehlík, Martin Klobušický
 * @date 13.5.2020
 */
public class MyStreet implements Street {
    private String id;
    private List<Coordinate> coordinates;
    private List<Stop> stops;
    private int trafficCoefficient = 1;
    private javafx.scene.shape.Line lineGui;
    private boolean closed = false;
    private List<Line> lines = new ArrayList<>();

    public MyStreet(String id, Coordinate[] coordinates) {
        this.id = id;
        this.coordinates = new ArrayList<>();
        this.coordinates.addAll(Arrays.asList(coordinates));
        this.stops = new ArrayList<>();
    }

    public MyStreet(String id, List<Coordinate> coordinates) {
        this.id = id;
        this.coordinates = coordinates;
        this.stops = new ArrayList<>();
    }

    /**
     * Add line to street
     * @param line line that we want to add
     */
    public void addLine(Line line) {
        lines.add(line);
    }

    /**
     * Get lines of street
     * @return lines of street
     */
    public List<Line> getLines() {
        return lines;
    }

    /**
     * check if Street is closed
     * @return true if street is closed
     */
    public boolean isClosed() {
        return closed;
    }

    /**
     * Check if street is open
     * @return true if street is open
     */
    public boolean isOpen() {
        return !closed;
    }

    /**
     * Close or open the street
     * @param closed boolean if we want to close or open street
     */
    public void setClosed(boolean closed) {
        this.closed = closed;
        Platform.runLater(() -> {
            if (closed) {
                this.lineGui.setStyle("-fx-stroke-width: 2; -fx-stroke: red;");
            } else {
                this.lineGui.setStyle("-fx-stroke-width: 2; -fx-stroke: black;");
            }
        });
    }

    /**
     * Set cooeficient of trafic on actual street
     * @param trafficCoefficient Coeficient that we want so set
     * @throws Exception
     */
    public void setTrafficCoefficient(int trafficCoefficient) throws Exception {
        if (trafficCoefficient < 1 || trafficCoefficient > 10) {
            throw new Exception("Traffic coefficient have to be in range 1..10! Value will remain unchanged.");
        }
        this.trafficCoefficient = trafficCoefficient;
    }

    /**
     * Get the trafic coeficient of actual street
     * @return Trafic coeficient of street
     */
    public int getTrafficCoefficient() {
        return trafficCoefficient;
    }

    /**
     * Vrátí identifikátor ulice.
     *
     * @return Identifikátor ulice.
     */
    public String getId() {
        return this.id;
    }

    /**
     * Vrátí seznam souřadnic definujících ulici. První v seznamu je vždy počátek a poslední v seznamu konec ulice.
     *
     * @return Seznam souřadnic ulice.
     */

    public List<Coordinate> getCoordinates() {
        return this.coordinates;
    }

    /**
     * Vrátí seznam zastávek na ulici.
     *
     * @return Seznam zastávek na ulici. Pokud ulize nemá žádnou zastávku, je seznam prázdný.
     */
    public List<Stop> getStops() {
        return this.stops;
    }

    /**
     * Přidá do seznamu zastávek novou zastávku.
     *
     * @param stop Nově přidávaná zastávka.
     * @return True if it was successful
     */
    public boolean addStop(Stop stop) {
        if (stop == null)
            return false;

        if (this.coordinates.size() <= 1)
            return false;

        for (int i = 0; i < this.coordinates.size() - 1; i++) {
            double x1 = this.coordinates.get(i).getX();
            double y1 = this.coordinates.get(i).getY();

            double x2 = this.coordinates.get(i + 1).getX();
            double y2 = this.coordinates.get(i + 1).getY();


            if (x1 == x2 && x1 == stop.getCoordinate().getX()) {
                if (y1 < y2) {
                    if (stop.getCoordinate().getY() >= y1 && stop.getCoordinate().getY() <= y2) {
                        this.stops.add(stop);
                        stop.setStreet(this);
                        return true;
                    } else {
                        return false;
                    }
                } else {
                    if (stop.getCoordinate().getY() <= y1 && stop.getCoordinate().getY() >= y2) {
                        this.stops.add(stop);
                        stop.setStreet(this);
                        return true;
                    } else {
                        return false;
                    }
                }
            }

            if (y1 == y2 && y1 == stop.getCoordinate().getY()) {
                if (x1 < x2) {
                    if (stop.getCoordinate().getX() >= x1 && stop.getCoordinate().getX() <= x2) {
                        this.stops.add(stop);
                        stop.setStreet(this);
                        return true;
                    } else {
                        return false;
                    }
                } else {
                    if (stop.getCoordinate().getX() <= x1 && stop.getCoordinate().getX() >= x2) {
                        this.stops.add(stop);
                        stop.setStreet(this);
                        return true;
                    } else {
                        return false;
                    }
                }

            }

            /*int diffX1 = coordinates.get(i).diffX(coordinates.get(i + 1));
            int diffY1 = coordinates.get(i).diffY(coordinates.get(i + 1));

            //y=ax+b
            //soustava dvou rovnic o dvou neznamych
            //y1=x1*a+b;
            //y2=x2*a+b;

            //y2=x2*a+b; *(-1)
            y2 *= -1;
            x2 *= -1;

            //x2-x1
            int x2_x1 = x2 - x1;

            //y2-y1
            int y2_y1 = y2 - y1;

            double a = (double) y2_y1 / x2_x1;
            double b = a*x1 + y1;

            //dosazeni bodu na kontrolu

            int x = stop.getCoordinate().getX();
            int y = stop.getCoordinate().getY();
            if(y == ((a * x) + b))
            {

                return true;
            }*/

        }

        return false;
    }

    /**
     * Function that compares streets
     * @param obj street that we eant compare to actual street
     * @return True if they are the same
     */
    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (this.getClass() != obj.getClass()) {
            return false;
        }
        Street street = (Street) obj;
        if (this.id.equals(street.getId())) {

            if (!this.coordinates.equals(street.getCoordinates()))
                return false;


            if (!this.stops.equals(street.getStops()))
                return false;


            return true;
        }
        return false;
    }

    /**
     * Starting Coordinate of street
     * @return first coordinate of street
     */
    @Override
    public Coordinate begin() {
        return this.coordinates.get(0);
    }

    /**
     * Get last coord of street
     * @return last coordinate of street
     */
    @Override
    public Coordinate end() {
        return this.coordinates.get(this.coordinates.size() - 1);
    }

    /**
     * Check if street follows actual street
     * @param s street that should follow actual street
     * @return true if streets are followed
     */
    @Override
    public boolean follows(Street s) {
        if (this.begin().equals(s.begin()) || this.begin().equals(s.end()) || this.end().equals(s.begin()) || this.end().equals(s.end())) {
            return true;
        }
        return false;
        /*List<Coordinate> coordinatesA = this.coordinates;
        List<Coordinate> coordinatesB = s.getCoordinates();

        for (Coordinate coordinateA : coordinatesA) {
            for (Coordinate coordinateB : coordinatesB) {
                if (coordinateA.equals(coordinateB))
                    return true;
            }
        }
        return false;*/
    }

    /**
     * Get stop on actual street
     * @param id id of stop
     * @return Stop that we are looking for
     */
    @Override
    public Stop getStop(String id) {
        for (Stop stop : this.stops) {
            if (stop.getId().equals(id))
                return stop;
        }
        return null;
    }

    /**
     * Draw street
     * @param gui GIU that we want to set
     */
    public void setGui(javafx.scene.shape.Line gui) {
        this.lineGui = gui;
    }

    /**
     * Return GIU
     * @return GUI
     */
    public javafx.scene.shape.Line getGui() {
        return lineGui;
    }
}
