package app.controllers;

import app.components.ZoomableScrollPane;
import app.components.ZoomingPane;
import app.model.maps.myMaps.*;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Slider;
import javafx.scene.input.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


public class Controller extends BaseController {

    public Pane mapPane;
    public GridPane gridPane;
    public ScrollPane scrollPane;
    public Slider zoomSlider;
    public Label zoomLabel;

    public StreetMap streetMap;

    public Controller() {
        streetMap = new MyStreetMap();
    }

    private double scale=1;

    private void addNodeToMapPane(Node node) {
        node.setLayoutX(node.getLayoutX()*this.scale);
        node.setLayoutY(node.getLayoutY()*this.scale);
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



    private JSONObject loadMap(String filePath)  {
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

                Line drawableLine = new Line(start.getX()*scale, start.getY()*scale, end.getX()*scale, end.getY()*scale);
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
    public void startUp() {
        this.gridPane.setAlignment(Pos.TOP_LEFT);
        this.gridPane.paddingProperty().setValue(new Insets(20, 20, 20, 20));

        this.scrollPane.prefHeightProperty().bind(this.gridPane.heightProperty());

        scrollPane.addEventFilter(ScrollEvent.SCROLL, Event::consume);

        this.mapPane = new Pane();

        ZoomingPane zoomingPane = new ZoomingPane(this.mapPane);
        zoomingPane.setStyle("-fx-border-color: green;");

        DoubleProperty zoomFactor = new SimpleDoubleProperty();
        zoomFactor.bind(zoomingPane.zoomFactorProperty());

        zoomFactor.addListener(new ChangeListener<Number>() {
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                mapPane.setPrefSize(mapPane.getWidth()*newValue.doubleValue(),mapPane.getHeight()*newValue.doubleValue());
            }
        });


        JSONObject map = loadMap("data/map.json");

        try {
            assert map != null;
            drawMap(map);
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }



        gridPane.setOnZoom(event -> {
            System.out.println("zoom");
        });


        scrollPane.setContent(zoomingPane);

        zoomingPane.zoomFactorProperty().bind(this.zoomSlider.valueProperty());
        this.zoomLabel.textProperty().bind(zoomSlider.valueProperty().asString());


    }



}
