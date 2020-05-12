package app.controllers;

import app.components.ZoomingPane;
import app.core.AlertHandler;
import app.core.ExceptionHandler;
import app.models.JSONLoader;
import app.models.maps.*;
import app.view.BaseGui;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

import java.io.*;
import java.time.LocalTime;
import java.util.*;


public class Controller extends BaseController {

    @FXML
    public Pane mapPane;
    @FXML
    public GridPane gridPane;
    @FXML
    public Slider zoomSlider;
    @FXML
    public Label zoomLabel;
    @FXML
    public ScrollPane scrollPane;
    @FXML
    public ZoomingPane zoomingPane;
    @FXML
    public Label currentTimeLabel;
    @FXML
    public Button startSimulationButton;
    @FXML
    public TextField simulationTimeTextField;
    @FXML
    public Slider simulationSpeedSlider;
    @FXML
    public Slider refreshIntervalSlider;
    @FXML
    public TableView<Object> activeVehiclesTableView;
    @FXML
    public Label selectedTripLabel;
    @FXML
    public TableView<Object> selectedTripTableView;

    private Simulator simulator;
    private BaseGui baseGui;

    private StreetMap streetMap;

    public Controller() {
        streetMap = new MyStreetMap();
    }


    private void addNodeToMapPane(Node node) {
        node.setLayoutX(node.getLayoutX());
        node.setLayoutY(node.getLayoutY());
        mapPane.getChildren().add(node);
    }


    private void addLabelOverStop(Label label, double x, double y) {
        Platform.runLater(() -> {
            addNodeToMapPane(label);
            mapPane.applyCss();
            mapPane.layout();
            double lengthLabel = label.getWidth() / 2;
            label.setLayoutX(x-lengthLabel);
            label.setLayoutY(y - 25);
        });

    }

    private void drawStops(Street street) {
        for (Stop stop : street.getStops()) {
            Circle circle = new Circle(stop.getCoordinate().getX(), stop.getCoordinate().getY(), 7, Paint.valueOf("blue"));
            Label label = new Label(stop.getId());
            addLabelOverStop(label, stop.getCoordinate().getX(), stop.getCoordinate().getY());
            Platform.runLater(() -> {
                mapPane.getChildren().remove(label);
            });

            circle.setOnMouseEntered(event -> {
                addLabelOverStop(label, stop.getCoordinate().getX(), stop.getCoordinate().getY());
            });
            circle.setOnMouseExited(event -> {
                Platform.runLater(() -> {
                    mapPane.getChildren().remove(label);
                });
            });

            addNodeToMapPane(circle);
        }
    }

    private double maxX = 0;
    private double maxY = 0;

    public void drawMap(JSONObject map) throws IOException, ParseException {


        JSONArray streets = (JSONArray) map.get("streets");

        for (JSONObject streetJson : (Iterable<JSONObject>) streets) {
            JSONArray coordinates = (JSONArray) streetJson.get("coordinates");

            // Get coordinates array
            List<Coordinate> listCoordinates = new ArrayList<>();
            for (JSONArray coord : (Iterable<JSONArray>) coordinates) {
                listCoordinates.add(new Coordinate((double)coord.get(0),(double)coord.get(1)));
            }

            // Try create street
            Street street = Street.create(streetJson.get("id").toString(), listCoordinates);
            if (street == null) {
                System.err.println("Error: Invalid format of input data!");
                continue;
            }

            // Add stops to street
            JSONArray stops = (JSONArray) streetJson.get("stops");

            for (JSONObject stop : (Iterable<JSONObject>) stops) {
                JSONArray coordinate = (JSONArray) stop.get("coordinates");
                Stop stopNew = Stop.defaultStop(stop.get("id").toString(), new Coordinate((double) coordinate.get(0), (double) coordinate.get(1)));

                street.addStop(stopNew);
            }

            // Draw street name
            Coordinate streetNameCoord = street.getCoordinates().get(0);
            double x = streetNameCoord.getX();
            double y = streetNameCoord.getY();
            Label streetName = new Label(street.getId());
            streetName.setLayoutX(x);
            streetName.setLayoutY(y + 5);
            addNodeToMapPane(streetName);

            // Draw street
            for (int i = 0; i < street.getCoordinates().size() - 1; i++) {
                Coordinate start = street.getCoordinates().get(i);
                Coordinate end = street.getCoordinates().get(i + 1);
                if (start.getX() > this.maxX) {
                    this.maxX = start.getX();
                }
                if (start.getY() > this.maxY) {
                    this.maxY = start.getY();
                }
                if (end.getX() > this.maxX) {
                    this.maxX = end.getX();
                }
                if (end.getY() > this.maxY) {
                    this.maxY = end.getY();
                }
                Line drawableLine = new Line(start.getX(), start.getY(), end.getX(), end.getY());
                drawableLine.setStyle("-fx-stroke-width: 2;");
                addNodeToMapPane(drawableLine);
                street.setGui(drawableLine);
                drawableLine.setOnMouseClicked(event -> {
                    if (!simulator.getSimulationState()) {
                        try {
                            Stage currentWindow = (Stage) this.selectedTripLabel.getScene().getWindow();

                            FXMLLoader fxmlLoader = new FXMLLoader();
                            fxmlLoader.setLocation(getClass().getResource("/app/fxml/streetManagement.fxml"));
                            Scene scene = new Scene(fxmlLoader.load(), 500, 150);
                            ((StreetSettingsController)fxmlLoader.getController()).startUp(street,currentWindow);
                            Stage stage = new Stage();
                            stage.setTitle("Street settings");
                            stage.setScene(scene);
                            stage.initOwner(currentWindow);
                            stage.initModality(Modality.APPLICATION_MODAL);
                            stage.showAndWait();
                            simulator.computeTraffic();
                            simulator.setLinesBlock();

                            /*TextInputDialog dialog = new TextInputDialog(Integer.toString(street.getTrafficCoefficient()));
                            dialog.setTitle("Street settings");
                            dialog.setHeaderText("Enter street traffic coefficient");
                            dialog.setContentText("You can set coefficient 1-10, where 1 is normal traffic and 10 is maximum traffic.");

                            Optional<String> result = dialog.showAndWait();*/
                            /*result.ifPresent(s -> {
                                try {
                                    street.setTrafficCoefficient(Integer.parseInt(s));
                                    simulator.computeTraffic();
                                }catch (NumberFormatException e)
                                {
                                    AlertHandler.showWarning(new Exception("Input is not integer!"));
                                }
                                catch (Exception e) {
                                    AlertHandler.showWarning(e);
                                }
                            });*/
                        } catch (Exception e) {
                            ExceptionHandler.show(e);
                        }
                    }

                });
            }

            drawStops(street);
            streetMap.addStreet(street);
        }
    }


    /**
     * Function that is called on Scene start up.
     */
    @Override
    public void startUp() {
        this.gridPane.setAlignment(Pos.TOP_LEFT);
        this.gridPane.paddingProperty().setValue(new Insets(20, 20, 20, 20));

        this.mapPane = new Pane();

        ZoomingPane zoomingPane = new ZoomingPane(this.mapPane);

        JSONObject map = null;
        try {
            map = JSONLoader.load("data/map.json");
        } catch (Exception e) {
            AlertHandler.showError(e);
        }

        try {
            assert map != null;
            drawMap(map);
        } catch (IOException | ParseException e) {
            AlertHandler.showError(e);
        }

        scrollPane.setContent(zoomingPane);

        zoomingPane.zoomFactorProperty().bind(zoomSlider.valueProperty());

        mapPane.setPrefSize(this.maxX + 50, this.maxY + 50);

        zoomSlider.valueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue observable, Number oldValue, Number newValue) {
                mapPane.setPrefSize((maxX * newValue.doubleValue() + 50), (maxY * newValue.doubleValue() + 50));
            }
        });

        this.zoomingPane = zoomingPane;

        this.simulationSpeedSlider.valueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                try {
                    simulator.setSimulationSpeed(newValue.doubleValue());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        this.refreshIntervalSlider.valueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                try {
                    simulator.setSimulationRefreshSpeed((int) Math.round(newValue.doubleValue()));
                } catch (Exception e) {
                    AlertHandler.showError(e);
                }
            }
        });

        TableColumn<Object, Object> routeId = new TableColumn<>("Route");
        routeId.setCellValueFactory(new PropertyValueFactory<>("routeId"));

        TableColumn<Object, Object> tripId = new TableColumn<>("Trip");
        tripId.setCellValueFactory(new PropertyValueFactory<>("tripId"));

        this.activeVehiclesTableView.getColumns().addAll(routeId, tripId);


        TableColumn<Object, Object> stopId = new TableColumn<>("Stop");
        stopId.setCellValueFactory(new PropertyValueFactory<>("stopId"));

        TableColumn<Object, Object> plannedTime = new TableColumn<>("Planned time");
        plannedTime.setCellValueFactory(new PropertyValueFactory<>("plannedTime"));

        TableColumn<Object, Object> actualTime = new TableColumn<>("Actual time");
        actualTime.setCellValueFactory(new PropertyValueFactory<>("actualTime"));

        this.selectedTripTableView.getColumns().addAll(stopId, plannedTime, actualTime);


        this.baseGui = new BaseGui(this);
        try {
            this.simulator = new Simulator(streetMap, this.baseGui);
        } catch (Exception e) {
            ExceptionHandler.show(e);
        }


    }

    /**
     * Function that is called on Scene close.
     */
    @Override
    public void close() {
        this.simulator.permanentTimer.cancel();
        this.simulator.stop();
    }


    public void startSimulationBtnOnClicked() {

        if(this.simulator.isConflict())
        {
            try {
                ExceptionHandler.throwException("You cant start simulation when there is unresolved conflict on some line!");
            } catch (Exception ignored) {
            }
            return;
        }
        if (this.simulator.getSimulationState()) {
            this.simulator.stop();
            this.baseGui.toggleSimulationButton(false);
        } else {
            try {
                this.simulator.start(LocalTime.parse(this.simulationTimeTextField.getText()));
            } catch (Exception e) {
                AlertHandler.showError(e);
                return;
            }

            this.baseGui.toggleSimulationButton(true);
        }


    }


    public void openLinesManagementClick(MouseEvent mouseEvent) {
        if(this.simulator.getSimulationState())
        {
            AlertHandler.showWarning(new Exception("You can not edit lines while simulation running!"));
        }else
        {
            Stage currentWindow = (Stage) this.selectedTripLabel.getScene().getWindow();

            FXMLLoader fxmlLoader = new FXMLLoader();
            fxmlLoader.setLocation(getClass().getResource("/app/fxml/linesManagement.fxml"));
            Scene scene = null;
            try {
                scene = new Scene(fxmlLoader.load(), 500, 500);
            } catch (IOException e) {
                e.printStackTrace();
                return;
            }
            ((LinesManagement)fxmlLoader.getController()).startUp(this.simulator.getLines(),this.streetMap);
            Stage stage = new Stage();
            stage.setTitle("Lines manager");
            stage.setScene(scene);
            stage.initOwner(currentWindow);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();
            simulator.setLinesBlock();
        }
    }

    /**
     * Find middle coordinate of street
     * @param street
     * @return
     */
    public Coordinate getStreetMid(Street street){
        Coordinate result = new Coordinate(0,0);
        if ((street.getCoordinates().get(0).getX() - street.getCoordinates().get(1).getX()) != 0){
            if ((street.getCoordinates().get(0).getX() - street.getCoordinates().get(1).getX()) < 0){
                result.setX( ((Math.abs(street.getCoordinates().get(0).getX() - street.getCoordinates().get(1).getX()))/2) + (street.getCoordinates().get(0).getX()));
            }
            else{
                result.setX( ((Math.abs(street.getCoordinates().get(0).getX() - street.getCoordinates().get(1).getX()))/2) + (street.getCoordinates().get(1).getX()));
            }
            result.setX(street.getCoordinates().get(0).getX());
        }
        else {
            if ((street.getCoordinates().get(0).getY() - street.getCoordinates().get(1).getY()) < 0){
                result.setY( ((Math.abs(street.getCoordinates().get(0).getY() - street.getCoordinates().get(1).getY()))/2) + (street.getCoordinates().get(0).getY()));
            }
            else{
                result.setY( ((Math.abs(street.getCoordinates().get(0).getX() - street.getCoordinates().get(1).getX()))/2) + (street.getCoordinates().get(1).getY()));
            }
            result.setY(street.getCoordinates().get(0).getX());
        }
        return result;
    }
}
