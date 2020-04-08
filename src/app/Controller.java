package app;

import app.model.maps.myMaps.*;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Line;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;


import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;


public class Controller {

    public Pane pane;

    public StreetMap streetMap;


    public Controller() {
        streetMap = new MyStreetMap();
    }

    public void drawMap() throws IOException, ParseException {


        File file = new File(
                Objects.requireNonNull(getClass().getClassLoader().getResource("map.json")).getFile()
        );

        File file1 = new File("./data/map.json");
        System.out.println(file1.getAbsolutePath());
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


            JSONArray stops = (JSONArray) tmp.get("stops");
            List<Stop> listStop = new ArrayList<>();
            for (JSONArray stop : (Iterable<JSONArray>) stops) {

            }

            Street street = Street.create(tmp.get("id").toString(), listCoordinates);
            if(street==null)
            {
                System.err.println("Error: Invalid format of input data!");
                continue;
            }
            for (int i = 0; i < street.getCoordinates().size() - 1; i++) {
                Coordinate start = street.getCoordinates().get(i);
                Coordinate end = street.getCoordinates().get(i + 1);

                Line drawableLine = new Line(start.getX(), start.getY(), end.getX(), end.getY());
                pane.getChildren().add(drawableLine);
            }


            streetMap.addStreet(street);
        }


    }
}
