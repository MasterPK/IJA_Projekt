package app.controllers;

import app.core.ExceptionHandler;
import app.models.TimeExtender;
import app.models.maps.Coordinate;
import app.models.maps.Line;
import app.models.maps.Street;
import app.models.maps.StreetMap;
import app.models.maps.*;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

/**
 * LineManagement controller. Sets detour of specific conflict in line when some street is closed.
 * @author Petr Křehlík, Martin Klobušický
 * @date 13.5.2020
 */
public class LineManagement {
    /**
     * Streets list of new detour.
     */
    public ListView newRouteListView;
    /**
     * Streets list that follows last good street in line.
     */
    public ListView selectStreetListView;
    /**
     * Actual line route.
     */
    public ListView currentRouteListView;
    /**
     * Label with name of last good stop.
     */
    public Label lastGoodStop;
    /**
     * Line object.
     */
    private Line line;
    /**
     * Full streets map.
     */
    private StreetMap streetMap;
    /**
     * Label with name of first good stop.
     */
    public Label firstGoodStop;

    /**
     * Initialize object, test if current conflict can be resolved (At least one detour is available) and initialize GUI.
     * @param line {@link LineManagement#line}
     * @param streetMap {@link LineManagement#streetMap}
     * @throws Exception When there is no route available.
     */
    public void startUp(Line line, StreetMap streetMap) throws Exception {
        this.line = line;
        this.streetMap = streetMap;
        if (!findWay(line.getStopByIndex(getIndexOfFirstStopThatIsGood(line, line.getConflicts().get(0).get(0))),
                line.getStopByIndex(getIndexOfLastStopThatIsGood(line, line.getConflicts().get(0).get(0))))) {
            ExceptionHandler.throwException("No other route is available because of closed street(s)!");
        }
        refreshGui();
    }

    /**
     * Refresh window GUI.
     * Find last good street.
     * Show available streets to pick.
     */
    private void refreshGui() {
        Platform.runLater(() -> {
            Street lastGoodStreet = null;
            boolean found = false;
            currentRouteListView.getItems().clear();
            for (Street street : line.getStreets()) {
                currentRouteListView.getItems().add(street.getId());
                if (street.isClosed()) {
                    found = true;

                }
                if (!found) {
                    lastGoodStreet = street;
                }
            }
            if (lastGoodStreet == null || line.getStreets().get(line.getStreets().size() - 1).equals(lastGoodStreet)) {
                return;
            }
            lastGoodStop.textProperty().setValue("Last open stop: " + line.getStops().get(getIndexOfFirstStopThatIsGood(this.line,this.line.getConflicts().get(0).get(0))).getId());
            firstGoodStop.textProperty().setValue("First open stop: " + line.getStops().get(getIndexOfLastStopThatIsGood(this.line,this.line.getConflicts().get(0).get(this.line.getConflicts().get(0).size()-1))).getId());
            List<Street> streets = getNextStreets(lastGoodStreet);
            for (Street street : streets) {
                if (street.isOpen()) {
                    this.selectStreetListView.getItems().add(street.getId());
                }
            }
        });
    }

    /**
     * Find all streets that follows specified street.
     * @param street Street.
     * @return Return list of streets.
     */
    public List<Street> getNextStreets(Street street) {
        List<Street> ulice = new ArrayList<>();

        Coordinate coord1 = street.getCoordinates().get(0);
        Coordinate coord2 = street.getCoordinates().get(1);


        for (Street str : this.streetMap.getStreets()) {
            if (str.getCoordinates().get(0).equals(coord1) || str.getCoordinates().get(0).equals(coord2)) {
                if (!street.equals(str)) {
                    ulice.add(str);
                }
            } else if (str.getCoordinates().get(1).equals(coord1) || str.getCoordinates().get(1).equals(coord2)) {
                if (!street.equals(str)) {
                    ulice.add(str);
                }
            }
        }
        return ulice;
    }

    /**
     * Click counter. Used to count double click on some cmponent.
     */
    private int clickCounter = 0;
    /**
     * Time of last click.
     */
    private LocalTime previous = LocalTime.now();

    /**
     * Add street to detour list.
     * Test if double click.
     * Add street to {@link LineManagement#newRouteListView}.
     * Compute new follow streets in {@link LineManagement#selectStreetListView}.
     * @param mouseEvent
     */
    public void addStreetClick(MouseEvent mouseEvent) {
        if (this.selectStreetListView.getSelectionModel().isEmpty()) {
            return;
        }
        clickCounter++;
        if (clickCounter == 1) {
            previous = LocalTime.now();
            return;
        }

        if (TimeExtender.minusLocalTime(LocalTime.now(), previous) > 1) {
            clickCounter = 0;
            return;
        }

        if (clickCounter == 2) {
            String streetId = (String) this.selectStreetListView.getSelectionModel().getSelectedItem();
            Platform.runLater(() -> {
                this.newRouteListView.getItems().add(streetId);
                this.selectStreetListView.getItems().clear();
                List<Street> streets = getNextStreets(this.streetMap.getStreet(streetId));
                for (Street street : streets) {
                    if (street.isOpen()) {
                        this.selectStreetListView.getItems().add(street.getId());
                    }

                }
            });
            clickCounter = 0;
        }

    }

    /**
     * Click counter. Used to count double click on some cmponent.
     */
    private int removeClickCounter = 0;
    /**
     * Time of last click.
     */
    private LocalTime previousRemove = LocalTime.now();

    /**
     * Remove street from detour list.
     * Test if double click.
     * Remove selected item from {@link LineManagement#newRouteListView}.
     * Compute new follow streets in {@link LineManagement#selectStreetListView}.
     * @param mouseEvent
     */
    public void removeStreetClick(MouseEvent mouseEvent) {
        if (this.newRouteListView.getSelectionModel().isEmpty()) {
            return;
        }
        removeClickCounter++;
        if (removeClickCounter == 1) {
            previous = LocalTime.now();
            return;
        }

        if (TimeExtender.minusLocalTime(LocalTime.now(), previous) > 1) {
            removeClickCounter = 0;
            return;
        }

        if (removeClickCounter == 2) {
            Platform.runLater(() -> {
                this.newRouteListView.getItems().remove(this.newRouteListView.getItems().get(this.newRouteListView.getItems().size() - 1));


                if (this.newRouteListView.getItems().isEmpty()) {
                    this.selectStreetListView.getItems().clear();
                    refreshGui();
                    removeClickCounter = 0;
                    return;
                }

                String streetId = (String) this.newRouteListView.getItems().get(this.newRouteListView.getItems().size() - 1);
                this.selectStreetListView.getItems().clear();
                List<Street> streets = getNextStreets(this.streetMap.getStreet(streetId));
                for (Street street : streets) {
                    if (street.isOpen()) {
                        this.selectStreetListView.getItems().add(street.getId());
                    }

                }
            });
            removeClickCounter = 0;
        }

    }

    /**
     * Find last stop before closed street.
     * @param line Line to search.
     * @param closedStreet Closed street.
     * @return Index of last open stop before closed street in line.
     */
    int getIndexOfFirstStopThatIsGood(Line line, Street closedStreet) {
        int indexOfFirstStopThatIsGood = -1;
        boolean found = false;
        for (int t = line.getStreets().indexOf(closedStreet) - 1; t >= 0; t--) {
            if (line.getStreets().get(t).isClosed()) {
                continue;
            }
            List<Stop> stops = new ArrayList<>();
            for (Stop stop : line.getStreets().get(t).getStops()) {
                if (line.getRealStops().contains(stop)) {
                    stops.add(stop);
                }
            }
            for (Stop stop : stops) {
                if (line.getRealStops().indexOf(stop) > indexOfFirstStopThatIsGood) {
                    indexOfFirstStopThatIsGood = line.getRealStops().indexOf(stop);
                    found = true;
                }
            }
            if (found) {
                return indexOfFirstStopThatIsGood;
            }
        }
        return indexOfFirstStopThatIsGood;
    }

    /**
     * Find first stop after closed street.
     * @param line Line to search.
     * @param closedStreet Closed street.
     * @return Index of first open stop after closed street.
     */
    int getIndexOfLastStopThatIsGood(Line line, Street closedStreet) {
        int indexOfLastStopThatIsGood = Integer.MAX_VALUE;
        boolean found = false;
        for (int t = line.getStreets().indexOf(closedStreet) + 1; t < line.getStreets().size(); t++) {
            if (line.getStreets().get(t).isClosed()) {
                continue;
            }
            List<Stop> stops = new ArrayList<>();
            for (Stop stop : line.getStreets().get(t).getStops()) {
                if (line.getRealStops().contains(stop)) {
                    stops.add(stop);

                }
            }
            for (Stop stop : stops) {
                if (line.getRealStops().indexOf(stop) < indexOfLastStopThatIsGood) {
                    indexOfLastStopThatIsGood = line.getRealStops().indexOf(stop);
                    found = true;
                }
            }
            if (found) {
                return indexOfLastStopThatIsGood;
            }
        }
        return indexOfLastStopThatIsGood;
    }

    /**
     * Update timetable based on new route with one closed street.
     * @deprecated
     * @param line Line object.
     * @param newStreets List of streets that is new route.
     * @param closedStreet Closed street.
     */
    public void updateTimetable(Line line, List<Street> newStreets, Street closedStreet) {

        double povodnaDlzka = 0;

        int indexOfLastStopThatIsGood = getIndexOfLastStopThatIsGood(line, closedStreet);
        int indexOfFirstStopThatIsGood = getIndexOfFirstStopThatIsGood(line, closedStreet);

        povodnaDlzka = line.getStopsLength(line.getStopByIndex(indexOfFirstStopThatIsGood), line.getStopByIndex(indexOfLastStopThatIsGood));

        int indexOfLastGoodStreet = line.getStreets().indexOf(line.getRealStops().get(indexOfFirstStopThatIsGood).getStreet());
        int indexOfFirstGoodStreet = line.getStreets().indexOf(line.getRealStops().get(indexOfLastStopThatIsGood).getStreet());

        int index = indexOfLastGoodStreet + 1;
        for (int i = indexOfLastGoodStreet + 1; i < indexOfFirstGoodStreet; i++) {
            line.getStreets().remove(index);
        }

        line.getStreets().addAll(index, newStreets);


        double objizdka = line.getStopsLength(line.getStopByIndex(indexOfFirstStopThatIsGood), line.getStopByIndex(indexOfLastStopThatIsGood));

        double actualPercent = 0;

        actualPercent = (objizdka / povodnaDlzka);
        for (Trip trip : line.getTrips()) {
            LocalTime time1 = trip.getActualTimetable().get(indexOfFirstStopThatIsGood);
            LocalTime time2 = trip.getActualTimetable().get(indexOfLastStopThatIsGood);
            double previousTime = TimeExtender.minusLocalTime(time2, time1);
            double newTime = (TimeExtender.minusLocalTime(time2, time1) * actualPercent);

            if (previousTime < newTime) {
                for (int k = indexOfLastStopThatIsGood; k < line.getStops().size(); k++) {
                    LocalTime tmp = trip.getActualTimetable().get(k);
                    trip.getPlannedTimetable().set(k, TimeExtender.plusLocalTime(tmp, (long) ((long) newTime - previousTime)));
                    trip.getActualTimetable().set(k, TimeExtender.plusLocalTime(tmp, (long) ((long) newTime - previousTime)));
                }
            } else {
                for (int k = indexOfLastStopThatIsGood; k < line.getStops().size(); k++) {
                    LocalTime tmp = trip.getActualTimetable().get(k);
                    trip.getPlannedTimetable().set(k, TimeExtender.minusLocalTime(tmp, (long) ((long) previousTime - newTime)));
                    trip.getActualTimetable().set(k, TimeExtender.minusLocalTime(tmp, (long) ((long) previousTime - newTime)));
                }
            }
        }
        if (closedStreet.getStops().size() > 0) {
            for (int j = 0; j < closedStreet.getStops().size(); j++) {
                if (line.getRealStops().contains(closedStreet.getStops().get(j))) {
                    for (Trip trip : line.getTrips()) {
                        trip.getPlannedTimetable().remove(line.getStops().indexOf(closedStreet.getStops().get(j)));
                        trip.getActualTimetable().remove(line.getStops().indexOf(closedStreet.getStops().get(j)));
                    }
                    line.getStops().remove(closedStreet.getStops().get(j));
                }

            }
        }

    }

    /**
     * Update timetable based on new route with list of closed street.
     * @param line Line object.
     * @param newStreets List of streets that is new route.
     * @param closedStreets List of closed streets that is in order. (Streets must follows each other)
     */
    public void updateTimetableNew(Line line, List<Street> newStreets, List<Street> closedStreets) {

        double povodnaDlzka = 0;

        int indexOfLastStopThatIsGood = getIndexOfLastStopThatIsGood(line, closedStreets.get(0));
        int indexOfFirstStopThatIsGood = getIndexOfFirstStopThatIsGood(line, closedStreets.get(closedStreets.size() - 1));

        povodnaDlzka = line.getStopsLength(line.getStopByIndex(indexOfFirstStopThatIsGood), line.getStopByIndex(indexOfLastStopThatIsGood));

        int indexOfLastGoodStreet = line.getStreets().indexOf(line.getRealStops().get(indexOfFirstStopThatIsGood).getStreet());
        int indexOfFirstGoodStreet = line.getStreets().indexOf(line.getRealStops().get(indexOfLastStopThatIsGood).getStreet());

        int index = indexOfLastGoodStreet + 1;
        for (int i = indexOfLastGoodStreet + 1; i < indexOfFirstGoodStreet; i++) {
            line.getStreets().remove(index);
        }

        line.getStreets().addAll(index, newStreets);


        double objizdka = line.getStopsLength(line.getStopByIndex(indexOfFirstStopThatIsGood), line.getStopByIndex(indexOfLastStopThatIsGood));

        double actualPercent = 0;

        actualPercent = (objizdka / povodnaDlzka);
        for (Trip trip : line.getTrips()) {
            LocalTime time1 = trip.getActualTimetable().get(indexOfFirstStopThatIsGood);
            LocalTime time2 = trip.getActualTimetable().get(indexOfLastStopThatIsGood);
            double previousTime = TimeExtender.minusLocalTime(time2, time1);
            double newTime = (TimeExtender.minusLocalTime(time2, time1) * actualPercent);

            if (previousTime < newTime) {
                for (int k = indexOfLastStopThatIsGood; k < line.getStops().size(); k++) {
                    LocalTime tmp = trip.getActualTimetable().get(k);
                    trip.getPlannedTimetable().set(k, TimeExtender.plusLocalTime(tmp, (long) ((long) newTime - previousTime)));
                    trip.getActualTimetable().set(k, TimeExtender.plusLocalTime(tmp, (long) ((long) newTime - previousTime)));
                }
            } else {
                for (int k = indexOfLastStopThatIsGood; k < line.getStops().size(); k++) {
                    LocalTime tmp = trip.getActualTimetable().get(k);
                    trip.getPlannedTimetable().set(k, TimeExtender.minusLocalTime(tmp, (long) ((long) previousTime - newTime)));
                    trip.getActualTimetable().set(k, TimeExtender.minusLocalTime(tmp, (long) ((long) previousTime - newTime)));
                }
            }
        }
        for (Street street : closedStreets) {
            if (street.getStops().size() > 0) {
                for (int j = 0; j < street.getStops().size(); j++) {
                    if (line.getRealStops().contains(street.getStops().get(j))) {
                        for (Trip trip : line.getTrips()) {
                            trip.getPlannedTimetable().remove(line.getStops().indexOf(street.getStops().get(j)));
                            trip.getActualTimetable().remove(line.getStops().indexOf(street.getStops().get(j)));
                        }
                        line.getStops().remove(street.getStops().get(j));
                    }

                }
            }
        }


    }

    /**
     * Compute new timetable.
     * Check if all data is correct.
     * Compute conflicts for next use.
     * Close window.
     * @param mouseEvent
     * @throws Exception When no route is available or route is not valid.
     */
    public void saveAndCloseClick(MouseEvent mouseEvent) throws Exception {
        List<Street> newStreets = new ArrayList<>();
        ObservableList list = this.newRouteListView.getItems();
        if (list.isEmpty()) {
            close();
            return;
        }
        for (Object streetId : list) {
            newStreets.add(this.streetMap.getStreet((String) streetId));
        }
        List<Street> closedStreets = this.line.getConflicts().get(0);


        if (!findWay(line.getStopByIndex(getIndexOfFirstStopThatIsGood(line, closedStreets.get(0))),
                line.getStopByIndex(getIndexOfLastStopThatIsGood(line, closedStreets.get(closedStreets.size() - 1))))) {
            line.restoreBackUp();
            ExceptionHandler.throwException("No route is available between specified stops because of closed street(s)!");
        }

        try {
            updateTimetableNew(this.line, newStreets, closedStreets);
        } catch (Exception e) {
            line.restoreBackUp();
            ExceptionHandler.throwException("Fatal error: Specified route is not valid!");
        }


        if (!lineCheck(line)) {
            line.restoreBackUp();
            ExceptionHandler.throwException("Line error: line is not valid!");
        }

        line.computeConflicts();

        close();
    }

    /**
     * Close window.
     */
    private void close() {
        Stage stage = (Stage) this.newRouteListView.getScene().getWindow();
        stage.close();
    }

    /**
     * Check if between two stops exists at least one available route.
     * @param startingStop First stop.
     * @param endingStop Second stop.
     * @return Return true if there is at least one not closed route. Otherwise false.
     */
    public boolean findWay(Stop startingStop, Stop endingStop) {
        List<Street> streetsToCheck = new ArrayList<>();
        List<Street> checkedStreets = new ArrayList<>();

        boolean result = false;

        Street startingStreet = startingStop.getStreet();
        Street endingStreet = endingStop.getStreet();

        Street checkingStreet;

        streetsToCheck.add(startingStreet);

        while (streetsToCheck.size() > 0) {
            checkingStreet = streetsToCheck.get(0);

            if (checkingStreet.equals(endingStreet)) {
                result = true;
                break;
            }

            checkedStreets.add(checkingStreet);

            streetsToCheck.addAll(followStreets(checkingStreet, checkedStreets));
            streetsToCheck.remove(0);
        }
        return result;
    }

    /**
     * Find streets that follows street.
     * @param street Street to be followed.
     * @param checked List of streets that have been already checked.
     * @return List of following streets.
     */
    private List<Street> followStreets(Street street, List<Street> checked) {
        List<Street> followStreets = new ArrayList<>();

        for (Street str : this.streetMap.getStreets()) {
            if ((str.getCoordinates().get(0).equals(street.getCoordinates().get(0))) || ((str.getCoordinates().get(0).equals(street.getCoordinates().get(1))))) {
                if (!checked.contains(str)) { //môže pridať ulicu ktorú kontrolujem..... ošetriť ASI
                    if (str.isOpen()) {
                        followStreets.add(str);
                    }
                }
            }
            if ((str.getCoordinates().get(1).equals(street.getCoordinates().get(0))) || ((str.getCoordinates().get(1).equals(street.getCoordinates().get(1))))) {
                if (!checked.contains(str)) {
                    if (str.isOpen()) {
                        followStreets.add(str);
                    }
                }
            }
        }
        return followStreets;
    }

    /**
     * Function that will check if streets in line are good
     *
     * @param line Line to check.
     * @return Return true if good, otherwise false.
     */
    private boolean lineCheck(Line line) {
        boolean result = true;

        for (int i = 0; i < line.getStreets().size() - 1; i++) {
            if (!line.isFollowing(line.getStreets().get(i), line.getStreets().get(i + 1))) {
                result = false;
            }
        }
        return result;
    }

}
