package app.controllers;

import app.models.maps.Line;
import javafx.scene.control.ListView;

public class LineManagement {
    public ListView newRouteListView;
    public ListView selectStreetListView;
    private Line line;

    public void startUp(Line line){
        this.line=line;
    }
}
