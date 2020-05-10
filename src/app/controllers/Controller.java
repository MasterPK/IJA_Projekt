package app.controllers;

import app.components.ZoomingPane;
import app.core.AlertHandler;
import app.core.ExceptionHandler;
import app.models.JSONLoader;
import app.models.maps.*;
import app.view.BaseGui;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.stage.Stage;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

import java.io.*;
import java.time.LocalTime;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;


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
    public TableView activeVehiclesTableView;
    @FXML
    public Label selectedTripLabel;
    @FXML
    public TableView selectedTripTableView;

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


    private void addLabelOverStop(String text, double x, double y) {
        Label label = new Label(text);
        label.setLayoutX(x);
        label.setLayoutY(y - 25);
        addNodeToMapPane(label);
    }

    private void drawStops(Street street) {
        for (Stop stop : street.getStops()) {
            Circle circle = new Circle(stop.getCoordinate().getX(), stop.getCoordinate().getY(), 7, Paint.valueOf("blue"));
            addLabelOverStop(stop.getId(), stop.getCoordinate().getX(), stop.getCoordinate().getY());
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
                            /*

                            !!! Kod pro zobrazení nového okna, zatím nechat zde !!!

                            FXMLLoader fxmlLoader = new FXMLLoader();
                            fxmlLoader.setLocation(getClass().getResource("/app/view/streetSettings.fxm"));
                            StreetSettingsController streetSettingsController = new StreetSettingsController(street);
                            fxmlLoader.setController(streetSettingsController);
                            Scene scene = new Scene(fxmlLoader.load(), 200, 100);
                            streetSettingsController.startUp();
                            Stage stage = new Stage();
                            stage.setTitle("Street settings");
                            stage.setScene(scene);
                            stage.show();
                            */
                            TextInputDialog dialog = new TextInputDialog(Integer.toString(street.getTrafficCoefficient()));
                            dialog.setTitle("Street settings");
                            dialog.setHeaderText("Enter street traffic coefficient");
                            dialog.setContentText("You can set coefficient 1-10, where 1 is normal traffic and 10 is maximum traffic.");

                            Optional<String> result = dialog.showAndWait();
                            result.ifPresent(s -> {
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
                            });
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
                simulator.setSimulationSpeed(newValue.doubleValue());
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

        TableColumn routeId = new TableColumn("Route");
        routeId.setCellValueFactory(new PropertyValueFactory<>("routeId"));

        TableColumn tripId = new TableColumn("Trip");
        tripId.setCellValueFactory(new PropertyValueFactory<>("tripId"));

        this.activeVehiclesTableView.getColumns().addAll(routeId, tripId);


        TableColumn stopId = new TableColumn("Stop");
        stopId.setCellValueFactory(new PropertyValueFactory<>("stopId"));

        TableColumn plannedTime = new TableColumn("Planned time");
        plannedTime.setCellValueFactory(new PropertyValueFactory<>("plannedTime"));

        TableColumn actualTime = new TableColumn("Actual time");
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
        this.simulator.stop();
    }


    public void startSimulationBtnOnClicked() {

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


}
