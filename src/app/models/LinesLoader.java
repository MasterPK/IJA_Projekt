package app.models;

import app.models.maps.*;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public abstract class LinesLoader {


    /**
     * Load lines from file lines.json. Create references in streetMap.
     * @param streetMap StreetMap to be referenced.
     * @return List of lines
     * @throws Exception Throws when file not exist or in bad format.
     */
    public static List<Line> load(StreetMap streetMap) throws Exception {
        List<Line> lines = new ArrayList<>();

        JSONArray jsonLines = (JSONArray) JSONLoader.load("data/lines.json").get("lines");

        for (JSONObject jsonLine : (Iterable<JSONObject>) jsonLines) {

            String lineId = (String) jsonLine.get("lineId");
            String lineName = (String) jsonLine.get("lineName");

            Line line = new MyLine(lineId, lineName);
            lines.add(line);

            // Load route
            for (JSONObject jsonRoute : (Iterable<JSONObject>) jsonLine.get("route")) {

                String streetName = (String) jsonRoute.get("street");
                Street street = streetMap.getStreet(streetName);
                if (street == null) {
                    throw new Exception("Street \"" + streetName + "\" specified in \"lines.json\" doesnt exist in \"map.json\"!");
                }

                String stopName = (String) jsonRoute.get("stop");
                Stop stop= street.getStop(stopName);
                if (stop != null) {
                    line.addStop(stop);
                }
                else {
                    line.addStreet(street);
                }
                street.addLine(line);
            }

            // Load trips
            for (JSONObject jsonTrip : (Iterable<JSONObject>) jsonLine.get("trips")) {
                String tripId = (String) jsonTrip.get("tripId");
                List<LocalTime> times = new ArrayList<>();

                for (String jsonTripTime : (Iterable<String>) jsonTrip.get("times")) {
                    times.add(LocalTime.parse(jsonTripTime));
                }
                Trip trip = new Trip(tripId,times);
                trip.setLine(line);
                if(line.getRealStopsCount() != trip.getTimetable().size())
                {
                    throw new Exception("Count of trip stops and line stops at line: \""+line.getId()+"\" and trip: \""+trip.getId()+"\" doesnt match!");
                }
                line.addTrip(trip);
            }



        }

        return lines;

    }
}
