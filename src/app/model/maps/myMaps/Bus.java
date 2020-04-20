package app.model.maps.myMaps;

import javafx.scene.shape.Circle;

public class Bus {
    private int id;
    private Circle guiCircle;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Bus(int id){
        this.id=id;
    }

    public Circle getGuiCircle() {
        return guiCircle;
    }

    public void setGuiCircle(Circle guiCircle) {
        this.guiCircle = guiCircle;
    }
}
