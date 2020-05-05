package app.view;

import app.controllers.Controller;
import app.models.maps.Coordinate;
import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.shape.Circle;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class BaseGui {
    private Controller controller;
    private List<Node> garbage = new ArrayList<>();

    public BaseGui(Controller controller) {
        this.controller = controller;
    }

    public void createDot(Coordinate coordinate) {
        Platform.runLater(() -> {
            Circle circle = new Circle(coordinate.getX(), coordinate.getY(), 1);
            addSimulationNode(circle);
        });
    }

    public void addSimulationNode(Node node) {
        Platform.runLater(() -> {
            this.controller.mapPane.getChildren().add(node);
            garbage.add(node);
        });
    }

    public void showTime(LocalTime time) {
        Platform.runLater(() -> {
            this.controller.currentTimeLabel.textProperty().setValue(time.format(DateTimeFormatter.ofPattern("HH:mm:ss")));
        });
    }

    public void clearSimulationGui() {
        for (Node node : garbage) {
            Platform.runLater(() -> {
                this.controller.mapPane.getChildren().remove(node);
            });
        }
    }

    public void toggleSimulationButton(final boolean state) {
        Platform.runLater(() -> {
            if (state) {
                this.controller.startSimulationButton.textProperty().setValue("Stop simulation");
                this.controller.currentTimeLabel.visibleProperty().setValue(true);
                this.controller.simulationTimeTextField.visibleProperty().setValue(false);
            } else {
                this.controller.startSimulationButton.textProperty().setValue("Start simulation");
                this.controller.currentTimeLabel.visibleProperty().setValue(false);
                this.controller.simulationTimeTextField.visibleProperty().setValue(true);
                this.controller.simulationTimeTextField.textProperty().setValue(this.controller.currentTimeLabel.getText());
            }
        });
    }

    public String getSimulationTimeFiledText()
    {
        return this.controller.simulationTimeTextField.getText();
    }
}
