package app.controllers;

import app.models.maps.Coordinate;
import app.models.maps.Line;
import app.models.maps.Street;
import javafx.application.Platform;
import javafx.scene.control.ListView;

import java.util.ArrayList;
import java.util.List;

public class LineManagement {
    public ListView newRouteListView;
    public ListView selectStreetListView;
    public ListView currentRouteListView;
    private Line line;

    public void startUp(Line line) {
        this.line = line;
        refreshGui();
    }

    private void refreshGui() {
        Platform.runLater(() -> {
            currentRouteListView.getItems().clear();
            for (Street street : line.getStreets()) {
                currentRouteListView.getItems().add(street.getId());
            }
        });
    }

    public List<Street> getNextStreets(Street street){
        List<Street> ulice = new ArrayList<>();

        Coordinate coord1 = street.getCoordinates().get(0);
        Coordinate coord2 = street.getCoordinates().get(1);

        for (Street str: line.getStreets()){
            if (str.getCoordinates().get(0).equals(coord1) || str.getCoordinates().get(0).equals(coord2)){
                ulice.add(str);
            }
            if (str.getCoordinates().get(1).equals(coord1) || str.getCoordinates().get(1).equals(coord2)){
                ulice.add(str);
            }
        }
        return ulice;
    }

}
