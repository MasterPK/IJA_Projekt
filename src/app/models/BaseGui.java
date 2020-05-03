package app.models;

import app.components.ZoomingPane;
import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;

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

    public void addNode(Node node)
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

    public void clearGui()
    {
        for(Node node:garbage)
        {
            Platform.runLater(() -> {
                mapPane.getChildren().remove(node);
            });
        }
    }
}
