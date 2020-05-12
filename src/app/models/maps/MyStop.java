package app.models.maps;


import app.view.BaseGui;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Line;

public class MyStop implements Stop
{
    private String id;
    private Coordinate coordinate;
    private Street street = null;
    public Label label;

    public MyStop(String id)
    {
        this.id = id;
    }

    public MyStop(String id, Coordinate coordinate)
    {
        this.id = id;
        this.coordinate = coordinate;
    }

    /**
     * Vrátí identifikátor zastávky.
     *
     * @return Identifikátor zastávky.
     */
    public String getId()
    {
        return this.id;
    }

    /**
     * Vrátí pozici zastávky.
     *
     * @return Pozice zastávky. Pokud zastávka existuje, ale dosud nemá umístění, vrací null.
     */
    public Coordinate getCoordinate()
    {
        return this.coordinate;
    }

    /**
     * Nastaví ulici, na které je zastávka umístěna.
     *
     * @param s Ulice, na které je zastávka umístěna.
     */
    public void setStreet(Street s)
    {
        this.street = s;
    }

    /**
     * Vrátí ulici, na které je zastávka umístěna.
     *
     * @return Ulice, na které je zastávka umístěna. Pokud zastávka existuje, ale dosud nemá umístění, vrací null.
     */
    public Street getStreet()
    {
        return this.street;
    }

    /**
     * Set label.
     *
     * @param label
     */
    @Override
    public void setLabel(Label label) {
        this.label=label;
    }

    /**
     * Get stop name label.
     *
     * @return
     */
    @Override
    public Label getLabel() {
        return this.label;
    }


    @Override
    public boolean equals(Object obj)
    {
        if (obj == null)
        {
            return false;
        }
        if (this.getClass() != obj.getClass())
        {
            return false;
        }
        Stop stop = (Stop) obj;
        if (this.id.equals(stop.getId()))
        {
            return true;
        }
        return false;
    }


    @Override
    public String toString() {
        return "stop(" + this.getId() + ")";
    }
}
