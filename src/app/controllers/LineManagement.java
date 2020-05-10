package app.controllers;

import app.models.maps.Line;
import app.models.maps.Street;
import javafx.application.Platform;
import javafx.scene.control.ListView;

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
}
