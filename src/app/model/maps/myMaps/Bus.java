package app.model.maps.myMaps;

import javafx.scene.shape.Circle;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Bus {
    private int id;
    private Circle guiCircle;
    private List<LocalTime> timetable = new ArrayList<>();

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Bus(int id, Circle gui){
        this.id=id;
    }

    public Circle getGuiCircle() {
        return guiCircle;
    }

    public void setGuiCircle(Circle guiCircle) {
        this.guiCircle = guiCircle;
    }
}
