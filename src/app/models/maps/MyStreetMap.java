package app.models.maps;



import java.util.ArrayList;
import java.util.List;

public class MyStreetMap implements StreetMap {

    private List<Street> streets;
    public MyStreetMap()
    {
        streets = new ArrayList<>();
    }

    /**
     * Přidá ulici do mapy.
     * @param s Objekt reprezentující ulici.
     */
    public void addStreet(Street s)
    {
        if (s==null)
            return;
        
        this.streets.add(s);
    }

    /**
     * Vrátí objekt reprezentující ulici se zadaným id.
     * @param id Identifikátor ulice.
     * @return Nalezenou ulici. Pokud ulice s daným identifikátorem není součástí mapy, vrací null.
     */
    public Street getStreet(String id)
    {
        for (Street street:this.streets)
        {
            if(street.getId().equals(id))
                return street;
        }
        return null;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MyStreetMap that = (MyStreetMap) o;
        return streets.equals(that.streets);
    }

    public List<Street> getStreets() {
        return streets;
    }
}
