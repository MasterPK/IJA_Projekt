/*
 * Zdrojové kódy josu součástí zadání 1. úkolu pro předmětu IJA v ak. roce 2019/2020.
 * (C) Radek Kočí
 */
package app.models.maps;

import java.util.Objects;

/**
 * Class for basic work with coordinates
 * @author Petr Křehlík, Martin Klobušický
 * @date 13.5.2020
 */
public class Coordinate implements Cloneable {

    private double x;
    private double y;

    /**
     * Get the X coordinate
     * @return return X coordinate
     */
    public double getX() {
        return x;
    }

    /**
     * Get the Y coordinate
     * @return return Y coordinate
     */
    public double getY() {
        return y;
    }

    /**
     * Set the X coordinate
     * @param set coordinate that we want to set
     */
    public void setX(double set){
        this.x = set;
    }

    /**
     * Set the Y coordinate
     * @param set coordinate that we want to set
     */
    public void setY(double set){
        this.y = set;
    }

    /**
     * Constructor
     * @param x x coordinate
     * @param y y coordinate
     */
    public Coordinate(double x, double y) {
        this.x = x;
        this.y = y;
    }

    /**
     * Create new coordinate
     * @param x x coordinate
     * @param y y coordinate
     * @return created coordinate
     */
    public static Coordinate create(int x, int y) {
        if (x < 0 || y < 0) {
            return null;
        } else {
            return new Coordinate(x, y);
        }

    }

    /**
     * Equals actual coordinate to other coordinate
     * @param obj coordinate that we want to eqals to this coordinate
     * @return true if they are the same
     */
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

    /**
     * difference between X coordinates
     * @param c second X coordinate
     * @return value of diff
     */
    public double diffX(Coordinate c) {
        return this.x-c.x;
    }

    /**
     * difference between Y coordinates
     * @param c second Y coordinate
     * @return value of diff
     */
    public double diffY(Coordinate c) {
        return this.y-c.y;
    }


    /**
     * Clone actual object
     * @return new clonned object
     * @throws CloneNotSupportedException error
     */
    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}
