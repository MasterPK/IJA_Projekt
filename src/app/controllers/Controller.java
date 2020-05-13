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

/**
 * Main controller that handle main.fxml events.
 * @author Petr Křehlík, Martin Klobušický
 * @date 13.5.2020
 */
public class Controller extends BaseController {

    /**
     * GUI components linked to FXML.
     */
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

    /**
     * Simulator controller object.
     */
    private Simulator simulator;
    /**
     * BaseGui that handles most of gui changes.
     */
    private BaseGui baseGui;

    /**
     * Street map that holds all street and stop objects.
     */
    private StreetMap streetMap;

    /**
     * Initialize street map implicitly.
     */
    public Controller() {
        streetMap = new MyStreetMap();
    }

    /**
     * Add node to {@link Controller#mapPane}. Node is implicitly drawn to gui.
     * @param node Node that will be added.
     */
    private void addNodeToMapPane(Node node) {
        node.setLayoutX(node.getLayoutX());
        node.setLayoutY(node.getLayoutY());
        try{
            mapPane.getChildren().add(node);
        }catch (Exception ignored)
        {

        }

    }

    /**
     * Draw name of stop at specified coordinates.
     * @param label Label object with street name.
     * @param x X coordinate.
     * @param y Y coordinate.
     */
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

    /**
     * Draw all stops on specified street.
     * @param street Street which stops will be drawn.
     */
    private void drawStops(Street street) {
        for (Stop stop : street.getStops()) {
            Circle circle = new Circle(stop.getCoordinate().getX(), stop.getCoordinate().getY(), 7, Paint.valueOf("blue"));
            Label label = new Label(stop.getId());
            stop.setLabel(label);
            addLabelOverStop(label, stop.getCoordinate().getX(), stop.getCoordinate().getY());
            try{


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
            }catch (Exception ignored)
            {

            }
        }
    }

    /**
     * Max founded X coordinate in map.
     */
    private double maxX = 0;
    /**
     * Max founded Y coordinate in map.
     */
    private double maxY = 0;

    /**
     * Draw full map.
     * @param map JSON map data source.
     * @throws IOException When JSON data file doesn't exist.
     * @throws ParseException When JSON data file is in bad format.
     */
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
            Platform.runLater(() -> {
                Label streetName = new Label(street.getId());
                addNodeToMapPane(streetName);
                mapPane.applyCss();
                mapPane.layout();
                Coordinate streetNameCoord = getLabelPos(street,streetName.getWidth(),streetName.getHeight());
                streetName.setLayoutX(streetNameCoord.getX());
                streetName.setLayoutY(streetNameCoord.getY());
            });

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
     * Sets GUI appearance and link GUI events.
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

        mapPane.setPrefSize(this.maxX + 500, this.maxY + 500);

        zoomSlider.valueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue observable, Number oldValue, Number newValue) {
                mapPane.setPrefSize((maxX * newValue.doubleValue() + 500), (maxY * newValue.doubleValue() + 500));
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
     * Stop simulator and permanent timer.
     */
    @Override
    public void close() {
        this.simulator.permanentTimer.cancel();
        this.simulator.stop();
    }


    /**
     * Handle click on start/stop simulation button.
     */
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


    /**
     * Open new window wih lines conflicts management.
     */
    public void openLinesManagementClick() {
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
     * Calculate Label position for street name
     * @param street
     * @return
     */
    public Coordinate getLabelPos(Street street, double labelLenght, double labelHeight){
        Coordinate result = new Coordinate(0,0);
        double labelHalfLenght = labelLenght/2;
        double labelHeightHalf = labelHeight/2;
        if ((street.getCoordinates().get(0).getX() - street.getCoordinates().get(1).getX()) != 0){
            if ((street.getCoordinates().get(0).getX() - street.getCoordinates().get(1).getX()) < 0){
                result.setX( ((Math.abs(street.getCoordinates().get(0).getX() - street.getCoordinates().get(1).getX()))/2) + (street.getCoordinates().get(0).getX()));
            }
            else{
                result.setX( ((Math.abs(street.getCoordinates().get(0).getX() - street.getCoordinates().get(1).getX()))/2) + (street.getCoordinates().get(1).getX()));
            }
            result.setY(street.getCoordinates().get(0).getY()+5);
            result.setX(result.getX()-labelHalfLenght);
        }
        else {
            if ((street.getCoordinates().get(0).getY() - street.getCoordinates().get(1).getY()) < 0){
                result.setY( ((Math.abs(street.getCoordinates().get(0).getY() - street.getCoordinates().get(1).getY()))/2) + (street.getCoordinates().get(0).getY()));
            }
            else{
                result.setY( ((Math.abs(street.getCoordinates().get(0).getY() - street.getCoordinates().get(1).getY()))/2) + (street.getCoordinates().get(1).getY()));
            }
            result.setY(result.getY() - labelHeightHalf);
            result.setX(street.getCoordinates().get(0).getX()+10);
        }
        return result;
    }
}
