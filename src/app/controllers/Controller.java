package app.controllers;

import app.components.ZoomingPane;
import app.model.maps.myMaps.*;
import app.models.BaseGui;
import app.models.Simulator;
import com.opencsv.CSVReader;
import com.sun.media.sound.InvalidFormatException;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Slider;
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

    public Pane mapPane;
    public GridPane gridPane;
    public Slider zoomSlider;
    public Label zoomLabel;
    public ScrollPane scrollPane;
    public ZoomingPane zoomingPane;
    public StreetMap streetMap;
    public Label currentTimeLabel;
    public Button startSimulationButton;
    private Simulator simulator;
    private BaseGui baseGui;
    public GridPane mapGrid;
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


    private JSONObject loadMap(String filePath) {
        File file = new File(
                Objects.requireNonNull(getClass().getClassLoader().getResource("map.json")).getFile()
        );

        File file1 = new File(filePath);
        //System.out.println(file1.getAbsolutePath());
        FileReader reader = null;
        try {
            reader = new FileReader(file1);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        }

        JSONParser jsonParser = new JSONParser();
        try {
            JSONObject jsonObject = (JSONObject) jsonParser.parse(reader);
            return jsonObject;
        } catch (IOException | ParseException e) {
            e.printStackTrace();
            return null;
        }

    }

    public double centerX = 0;
    public double centerY = 0;
    public double oldCenterX = 0;
    public double oldCenterY = 0;

    private double maxX = 0;
    private double maxY = 0;

    public void drawMap(JSONObject map) throws IOException, ParseException {


        JSONArray streets = (JSONArray) map.get("streets");

        for (JSONObject tmp : (Iterable<JSONObject>) streets) {
            JSONArray coordinates = (JSONArray) tmp.get("coordinates");
            List<Coordinate> listCoordinates = new ArrayList<>();
            for (JSONArray coord : (Iterable<JSONArray>) coordinates) {
                listCoordinates.add(new Coordinate(Math.toIntExact((long) coord.get(0)), Math.toIntExact((long) coord.get(1))));
            }


            Street street = Street.create(tmp.get("id").toString(), listCoordinates);
            if (street == null) {
                System.err.println("Error: Invalid format of input data!");
                continue;
            }


            JSONArray stops = (JSONArray) tmp.get("stops");
            List<Stop> listStop = new ArrayList<>();
            for (JSONObject stop : (Iterable<JSONObject>) stops) {
                JSONArray coordinate = (JSONArray) stop.get("coordinates");
                Stop stopNew = Stop.defaultStop(stop.get("id").toString(), new Coordinate(Math.toIntExact((long) coordinate.get(0)), Math.toIntExact((long) coordinate.get(1))));

                street.addStop(stopNew);
            }

            Coordinate streetNameCoord = street.getCoordinates().get(0);
            int x = streetNameCoord.getX();
            int y = streetNameCoord.getY();
            Label streetName = new Label(street.getId());
            streetName.setLayoutX(x);
            streetName.setLayoutY(y + 5);
            addNodeToMapPane(streetName);

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
        //this.mapPane.setMaxSize(1.0,1.0);
        this.mapPane.setStyle("-fx-border-color: green;");

        ZoomingPane zoomingPane = new ZoomingPane(this.mapPane);
        //zoomingPane.setMaxSize(1.0,1.0);
        zoomingPane.setStyle("-fx-border-color: orange;");

        JSONObject map = loadMap("data/map.json");

        try {
            assert map != null;
            drawMap(map);
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }

        scrollPane.setContent(zoomingPane);


        zoomingPane.zoomFactorProperty().bind(zoomSlider.valueProperty());
        this.zoomLabel.textProperty().bind(zoomSlider.valueProperty().asString());

        mapPane.setPrefSize(this.maxX + 50, this.maxY + 50);

        zoomSlider.valueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue observable, Number oldValue, Number newValue) {
                mapPane.setPrefSize((maxX * newValue.doubleValue() + 50), (maxY * newValue.doubleValue() + 50));
            }
        });

        this.zoomingPane = zoomingPane;
        this.centerX = this.zoomingPane.getLayoutX();
        this.centerY = this.zoomingPane.getLayoutY();

        this.baseGui = new BaseGui(mapPane,currentTimeLabel);
        try {
            this.simulator = new Simulator(streetMap,LocalTime.now(),this.baseGui);
        } catch (Exception e) {
            e.printStackTrace();
        }


    }



    public void startSimulationBtnOnClicked() {
        this.simulator.start();
        Platform.runLater(() -> {
            this.startSimulationButton.textProperty().setValue("Stop simulation");
        });
    }


}
