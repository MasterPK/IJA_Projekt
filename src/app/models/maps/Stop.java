/*
 * Zdrojové kódy josu součástí zadání 1. úkolu pro předmětu IJA v ak. roce 2019/2020.
 * (C) Radek Kočí
 */
package app.models.maps;


import javafx.scene.control.Label;

/**
 * Interface for Stops
 * @author Petr Křehlík, Martin Klobušický
 * @date 13.5.2020
 */
public interface Stop {

    /**
     * Vrátí identifikátor zastávky.
     * @return Identifikátor zastávky.
     */
    public String getId();
    
    /**
     * Vrátí pozici zastávky.
     * @return Pozice zastávky. Pokud zastávka existuje, ale dosud nemá umístění, vrací null.
     */
    public Coordinate getCoordinate();

    /**
     * Nastaví ulici, na které je zastávka umístěna.
     * @param s Ulice, na které je zastávka umístěna.
     */
    public void setStreet(Street s);

    /**
     * Vrátí ulici, na které je zastávka umístěna.
     * @return Ulice, na které je zastávka umístěna. Pokud zastávka existuje, ale dosud nemá umístění, vrací null.
     */
    public Street getStreet();

    static Stop defaultStop(String id, Coordinate c) {
        return new MyStop(id,c);
    }

    /**
     * Set label.
     * @param label
     */
    void setLabel(Label label);

    /**
     * Get stop name label.
     * @return
     */
    Label getLabel();
}
