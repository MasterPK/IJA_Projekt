package app.controllers;

import app.model.maps.myMaps.*;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
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
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


public class Controller extends BaseController {

    public Pane mapPane;
    public GridPane gridPane;

    public StreetMap streetMap;

    public Controller() {
        streetMap = new MyStreetMap();
    }

    private void addNodeToMapPane(Node node)
    {
        mapPane.getChildren().add(node);
    }

    private void addLabelOverStop(String text, int x, int y)
    {
        Label label = new Label(text);
        label.setLayoutX(x);
        label.setLayoutY(y-25);
        addNodeToMapPane(label);
    }

    private void drawStops(Street street)
    {
        for(Stop stop:street.getStops())
        {
            Circle circle = new Circle(stop.getCoordinate().getX(),stop.getCoordinate().getY(),5,Paint.valueOf("blue"));
            addLabelOverStop(stop.getId(),stop.getCoordinate().getX(),stop.getCoordinate().getY());
            addNodeToMapPane(circle);
        }
    }

    public void drawMap() throws IOException, ParseException {

        File file = new File(
                Objects.requireNonNull(getClass().getClassLoader().getResource("map.json")).getFile()
        );

        File file1 = new File("data/map.json");
        //System.out.println(file1.getAbsolutePath());
        FileReader reader = new FileReader(file1);

        JSONParser jsonParser = new JSONParser();
        JSONObject jsonObject = (JSONObject) jsonParser.parse(reader);

        JSONArray streets = (JSONArray) jsonObject.get("streets");

        for (JSONObject tmp : (Iterable<JSONObject>) streets) {
            JSONArray coordinates = (JSONArray) tmp.get("coordinates");
            List<Coordinate> listCoordinates = new ArrayList<>();
            for (JSONArray coord : (Iterable<JSONArray>) coordinates) {
                listCoordinates.add(new Coordinate(Math.toIntExact((long) coord.get(0)), Math.toIntExact((long) coord.get(1))));
            }




            Street street = Street.create(tmp.get("id").toString(), listCoordinates);
            if(street==null)
            {
                System.err.println("Error: Invalid format of input data!");
                continue;
            }


            JSONArray stops = (JSONArray) tmp.get("stops");
            List<Stop> listStop = new ArrayList<>();
            for (JSONObject stop : (Iterable<JSONObject>) stops) {
                JSONArray coordinate = (JSONArray) stop.get("coordinates");
                Stop stopNew = Stop.defaultStop(stop.get("id").toString(),new Coordinate(Math.toIntExact((long) coordinate.get(0)),Math.toIntExact((long) coordinate.get(1))));

                street.addStop(stopNew);
            }

            Coordinate streetNameCoord = street.getCoordinates().get(0);
            int x = streetNameCoord.getX();
            int y = streetNameCoord.getY();
            Label streetName = new Label(street.getId());
            streetName.setLayoutX(x);
            streetName.setLayoutY(y+5);
            addNodeToMapPane(streetName);

            for (int i = 0; i < street.getCoordinates().size() - 1; i++) {
                Coordinate start = street.getCoordinates().get(i);
                Coordinate end = street.getCoordinates().get(i + 1);

                Line drawableLine = new Line(start.getX(), start.getY(), end.getX(), end.getY());
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
        gridPane.setAlignment(Pos.TOP_LEFT);
        gridPane.paddingProperty().setValue(new Insets(20,20,20,20));
    }
}
