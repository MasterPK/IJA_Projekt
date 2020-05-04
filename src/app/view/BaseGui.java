package app.view;

import app.models.maps.Coordinate;
import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Circle;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class BaseGui {
    private Pane mapPane;
    private List<Node> garbage = new ArrayList<>();
    private Label currentTimeLabel;

    public BaseGui(Pane mapPane, Label currentTimeLabel)
    {
        this.mapPane=mapPane;
        this.currentTimeLabel=currentTimeLabel;
    }

    public void createDot(Coordinate coordinate)
    {
        Platform.runLater(() -> {
            Circle circle = new Circle(coordinate.getX(),coordinate.getY(),1);
            addSimulationNode(circle);
        });
    }

    public void addSimulationNode(Node node)
    {
        Platform.runLater(() -> {
            mapPane.getChildren().add(node);
            garbage.add(node);
        });
    }

    public void showTime(LocalTime time)
    {
        Platform.runLater(() -> {
            currentTimeLabel.textProperty().setValue(time.format(DateTimeFormatter.ofPattern("HH:mm:ss")));
        });
    }

    public void clearSimulationGui()
    {
        for(Node node:garbage)
        {
            Platform.runLater(() -> {
                mapPane.getChildren().remove(node);
            });
        }
    }
}