/*
 * Zdrojové kódy josu součástí zadání 1. úkolu pro předmětu IJA v ak. roce 2019/2020.
 * (C) Radek Kočí
 */
package app.models.maps;

import java.util.Objects;

/**
 * Reprezentuje pozici (souřadnice) v mapě. Souřadnice je dvojice (x,y), počátek mapy je vždy na pozici (0,0).
 * Nelze mít pozici se zápornou souřadnicí.
 *
 * @author koci
 */
public class Coordinate implements Cloneable {

    private double x;
    private double y;

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }


    public void setX(double set){
        this.x = set;
    }
    public void setY(double set){
        this.y = set;
    }

    public Coordinate(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public static Coordinate create(int x, int y) {
        if (x < 0 || y < 0) {
            return null;
        } else {
            return new Coordinate(x, y);
        }

    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (this.getClass() != obj.getClass()) {
            return false;
        }
        Coordinate coordinate = (Coordinate) obj;
        if (this.x == coordinate.getX() && this.y == coordinate.getY()) {
            return true;
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }

    public double diffX(Coordinate c) {
        return this.x-c.x;
    }

    public double diffY(Coordinate c) {
        return this.y-c.y;
    }


    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}
