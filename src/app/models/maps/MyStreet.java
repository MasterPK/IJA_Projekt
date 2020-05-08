package app.models.maps;


import javafx.scene.shape.Line;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MyStreet implements Street {
    private String id;
    private List<Coordinate> coordinates;
    private List<Stop> stops;
    private int trafficCoefficient = 1;
    private Line lineGui;

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

    public void setTrafficCoefficient(int trafficCoefficient) throws Exception {
        if(trafficCoefficient < 1 || trafficCoefficient > 10)
        {
            throw new Exception("Traffic coefficient have to be in range 1..10! Value will remain unchanged.");
        }
        this.trafficCoefficient = trafficCoefficient;
    }

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
     * @return
     */
    public boolean addStop(Stop stop) {
        if (stop == null)
            return false;

        if (this.coordinates.size() <= 1)
            return false;

        for (int i = 0; i < this.coordinates.size() - 1; i++) {
            int x1 = this.coordinates.get(i).getX();
            int y1 = this.coordinates.get(i).getY();

            int x2 = this.coordinates.get(i + 1).getX();
            int y2 = this.coordinates.get(i + 1).getY();


            if (x1 == x2 && x1==stop.getCoordinate().getX()) {
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

            if (y1 == y2 && y1==stop.getCoordinate().getY()) {
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

    @Override
    public Coordinate begin() {
        return this.coordinates.get(0);
    }

    @Override
    public Coordinate end() {
        return this.coordinates.get(this.coordinates.size() - 1);
    }

    @Override
    public boolean follows(Street s) {
        if(this.begin().equals(s.begin()) || this.begin().equals(s.end()) || this.end().equals(s.begin()) || this.end().equals(s.end())  )
        {
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

    @Override
    public Stop getStop(String id) {
        for (Stop stop:this.stops)
        {
            if(stop.getId().equals(id))
                return stop;
        }
        return null;
    }

    public void setGui(Line gui) {
        this.lineGui = gui;
    }

    public Line getGui() {
        return lineGui;
    }
}
