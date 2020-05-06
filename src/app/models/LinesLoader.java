package app.models;

import app.models.maps.*;

import java.util.ArrayList;
import java.util.List;

public abstract class LinesLoader {


    public static List<Line> load(StreetMap streetMap) throws Exception{
        List<Line> lines = loadLines();
        loadTrips(lines);
        loadTimetable(lines,streetMap);
        return lines;
    }


    private static List<Line> loadLines() throws Exception {
        List<String[]> list = CSVLoader.load("data/routes.txt", new String[]{"route_id", "route_short_name"});
        List<Line> result = new ArrayList<>();
        for (String[] string : list) {
            String routeId = string[0];
            String routeShortTime = string[1];
            Line route = new MyLine(routeId, routeShortTime);
            result.add(route);
        }
        return result;
    }

    private static void loadTrips(List<Line> lines) throws Exception {

        List<String[]> list = CSVLoader.load("data/trips.txt", new String[]{"route_id", "trip_id"});

        for (String[] string : list) {
            String routeId = string[0];
            String tripId = string[1];

            for (Line line : lines) {
                if (!routeId.equals(line.getId())) {
                    throw new Exception("Route " + routeId + " doesnt exist!");
                }
                Trip trip = new Trip(tripId);
                line.addTrip(trip);
            }
        }
    }

    private static void loadTimetable(List<Line> lines, StreetMap streetMap) throws Exception {
        List<String[]> list = CSVLoader.load("data/stop_times.txt", new String[]{"trip_id", "time", "stop_id", "street_id"});

        for (String[] string : list) {
            String tripId = string[0];
            String time = string[1];
            String stopId = string[2];
            String streetId = string[3];
            for (Line line : lines) {
                for (Trip trip : line.getTrips()) {
                    if (trip.getId().equals(tripId)) {
                        for (Street street : streetMap.getStreets()) {
                            if (streetId.equals(street.getId())) {
                                if (stopId.isEmpty()) {
                                    if (time.isEmpty()) {
                                        line.addStreet(street);
                                    }
                                } else {
                                    for (Stop stop : street.getStops()) {
                                        if (stop.getId().equals(stopId)) {
                                            line.addStop(stop);
                                            trip.addTimetableItem(time);
                                            break;
                                        }
                                    }
                                }
                            }
                        }
                    }

                }
            }
        }
    }
}
