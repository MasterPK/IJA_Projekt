package app.controllers;

import app.components.ZoomingPane;
import app.models.JSONLoader;
import app.models.maps.*;
import app.view.BaseGui;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
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

    private Simulator simulator;
    private BaseGui baseGui;

    private StreetMap streetMap;


    private double scale = 1;

    public Controller() {
        streetMap = new MyStreetMap();
    }



    private void addNodeToMapPane(Node node) {
        node.setLayoutX(node.getLayoutX() * this.scale);
        node.setLayoutY(node.getLayoutY() * this.scale);
        mapPane.getChildren().add(node);
    }


    private void addLabelOverStop(String text, int x, int y) {
        Label label = new Label(text);
        label.setLayoutX(x);
        label.setLayoutY(y - 25);
        addNodeToMapPane(label);
    }

    private void drawStops(Street street) {
        for (Stop stop : street.getStops()) {
            Circle circle = new Circle(stop.getCoordinate().getX(), stop.getCoordinate().getY(), 5, Paint.valueOf("blue"));
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
                listCoordinates.add(new Coordinate(Math.toIntExact((long) coord.get(0)), Math.toIntExact((long) coord.get(1))));
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
                Stop stopNew = Stop.defaultStop(stop.get("id").toString(), new Coordinate(Math.toIntExact((long) coordinate.get(0)), Math.toIntExact((long) coordinate.get(1))));

                street.addStop(stopNew);
            }

            // Draw street name
            Coordinate streetNameCoord = street.getCoordinates().get(0);
            int x = streetNameCoord.getX();
            int y = streetNameCoord.getY();
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
                Line drawableLine = new Line(start.getX() * scale, start.getY() * scale, end.getX() * scale, end.getY() * scale);
                addNodeToMapPane(drawableLine);

            }

            drawStops(street);
            streetMap.addStreet(street);
        }
    }


    /**
     * Function that is called on Scene start up.
     */
    @Override
    public void startUp(){
        this.gridPane.setAlignment(Pos.TOP_LEFT);
        this.gridPane.paddingProperty().setValue(new Insets(20, 20, 20, 20));

        this.mapPane = new Pane();

        ZoomingPane zoomingPane = new ZoomingPane(this.mapPane);

        JSONObject map = null;
        try {
            map = JSONLoader.load("data/map.json");
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            assert map != null;
            drawMap(map);
        } catch (IOException | ParseException e) {
            e.printStackTrace();
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
                    e.printStackTrace();
                }
            }
        });


        this.baseGui = new BaseGui(this);
        try {
            this.simulator = new Simulator(streetMap,this.baseGui);
        } catch (Exception e) {
            e.printStackTrace();
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

        if(this.simulator.getSimulationState())
        {
            this.simulator.stop();
            this.baseGui.toggleSimulationButton(false);
        }
        else
        {
            try{
                this.simulator.start(LocalTime.parse(this.simulationTimeTextField.getText()));
            }catch (Exception ignored)
            {
                return;
            }

            this.baseGui.toggleSimulationButton(true);
        }


    }


}
