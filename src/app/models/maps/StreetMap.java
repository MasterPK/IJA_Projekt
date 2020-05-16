/*
 * Zdrojové kódy josu součástí zadání 1. úkolu pro předmětu IJA v ak. roce 2019/2020.
 * (C) Radek Kočí
 */
package app.models.maps;

import java.util.List;

/**
 * Interface for StreetMap
 * @author Petr Křehlík, Martin Klobušický
 * @date 13.5.2020
 */
public interface StreetMap {
    /**
     * Přidá ulici do mapy.
     * @param s Objekt reprezentující ulici.
     */
    public void addStreet(Street s);
    
    /**
     * Vrátí objekt reprezentující ulici se zadaným id.
     * @param id Identifikátor ulice.
     * @return Nalezenou ulici. Pokud ulice s daným identifikátorem není součástí mapy, vrací null.
     */
    public Street getStreet(String id);
    /**
     * Get streets on actual map.
     * @return List of streets of map.
     */
    List<Street> getStreets();
}
