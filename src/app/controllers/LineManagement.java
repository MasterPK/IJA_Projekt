package app.controllers;

import app.core.AlertHandler;
import app.models.TimeExtender;
import app.models.maps.Coordinate;
import app.models.maps.Line;
import app.models.maps.Street;
import app.models.maps.StreetMap;
import app.models.maps.*;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import sun.plugin2.jvm.CircularByteBuffer;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class LineManagement {
    public ListView newRouteListView;
    public ListView selectStreetListView;
    public ListView currentRouteListView;
    public Label lastGoooooodStreet;
    private Line line;
    private StreetMap streetMap;

    public void startUp(Line line,StreetMap streetMap) {
        this.line = line;
        this.streetMap=streetMap;
        refreshGui();
    }

    private void refreshGui() {
        Platform.runLater(() -> {
            Street lastGoodStreet=null;
            boolean found=false;
            currentRouteListView.getItems().clear();
            for (Street street : line.getStreets()) {
                currentRouteListView.getItems().add(street.getId());
                if(street.isClosed())
                {
                    found=true;

                }
                if(!found)
                {
                    lastGoodStreet=street;
                }
            }
            if(lastGoodStreet==null || line.getStreets().get(line.getStreets().size()-1).equals(lastGoodStreet))
            {
                return;
            }
            lastGoooooodStreet.textProperty().setValue("Last open street: "+lastGoodStreet.getId());
            List<Street> streets = getNextStreets(lastGoodStreet);
            for(Street street:streets)
            {
                if(street.isOpen())
                {
                    this.selectStreetListView.getItems().add(street.getId());
                }
            }
        });
    }

    public List<Street> getNextStreets(Street street){
        List<Street> ulice = new ArrayList<>();

        Coordinate coord1 = street.getCoordinates().get(0);
        Coordinate coord2 = street.getCoordinates().get(1);


        for (Street str: this.streetMap.getStreets()){
            if (str.getCoordinates().get(0).equals(coord1) || str.getCoordinates().get(0).equals(coord2)){
                if (!street.equals(str))
                {
                    ulice.add(str);
                }
            }
            else if (str.getCoordinates().get(1).equals(coord1) || str.getCoordinates().get(1).equals(coord2)){
                if (!street.equals(str))
                {
                    ulice.add(str);
                }
            }
        }
        return ulice;
    }

    private int clickCounter=0;
    private LocalTime previous=LocalTime.now();
    public void addStreetClick(MouseEvent mouseEvent) {
        if(this.selectStreetListView.getSelectionModel().isEmpty())
        {
            return;
        }
        clickCounter++;
        if(clickCounter==1)
        {
            previous=LocalTime.now();
            return;
        }

        if(TimeExtender.minusLocalTime(LocalTime.now(),previous)>1)
        {
            clickCounter=0;
            return;
        }

        if(clickCounter == 2){
            String streetId =(String) this.selectStreetListView.getSelectionModel().getSelectedItem();
            Platform.runLater(() -> {
                this.newRouteListView.getItems().add(streetId);
                this.selectStreetListView.getItems().clear();
                List<Street> streets = getNextStreets(this.streetMap.getStreet(streetId));
                for(Street street:streets)
                {
                    if(street.isOpen())
                    {
                        this.selectStreetListView.getItems().add(street.getId());
                    }

                }
            });

        }

    }
    public void updateTimetable(Line line, List<Street> newStreets, Street closedStreet){
        int indexOfFirstStopThatIsDeleted = 0;
        int indexOfLastStopThatIsDeleted = 0;
        int indexOfFirstStopThatIsGood=0;
        int indexOfLastStopThatIsGood=0;
        int indexOfClosed = line.getStreets().indexOf(closedStreet);
        double povodnaDlzka = 0;

        /*if (closedStreet.getStops().size() > 0){
            indexOfFirstStopThatIsDeleted = line.getStops().indexOf(closedStreet.getStops().get(0));
            indexOfLastStopThatIsDeleted = line.getStops().indexOf(closedStreet.getStops().size()-1);
            povodnaDlzka = line.getStopsLength(line.getStopByIndex(indexOfFirstStopThatIsDeleted-1),line.getStopByIndex(indexOfLastStopThatIsDeleted+1));
        }
        else{*/
            for (int t = line.getStreets().indexOf(closedStreet)-1;t>=0;t--){
                for (Stop stop:line.getStreets().get(t).getStops()){
                    if (line.getRealStops().contains(stop)){
                        indexOfFirstStopThatIsGood = line.getStops().lastIndexOf(stop);
                    }
                }
            }
            ahoj2:
            for (int t = line.getStreets().indexOf(closedStreet);t<line.getStreets().size();t++){
                for (Stop stop:line.getStreets().get(t).getStops()){
                    if (line.getRealStops().contains(stop)){
                        indexOfLastStopThatIsGood = line.getStops().indexOf(stop);
                        break ahoj2;
                    }
                }
            }
            povodnaDlzka = line.getStopsLength(line.getStopByIndex(indexOfFirstStopThatIsGood),line.getStopByIndex(indexOfLastStopThatIsGood));
        //}

        line.getStreets().remove(closedStreet);
        for (int i = 0; i<newStreets.size();i++,indexOfClosed++){
            line.getStreets().add(indexOfClosed,newStreets.get(i));
        }
        if (closedStreet.getStops().size() > 0){
            for (int j = 0;j<closedStreet.getStops().size();j++){
                for (Trip trip:line.getTrips()){
                    trip.getTimetable().remove(line.getStops().indexOf(j));
                    trip.getActualTimetable().remove(line.getStops().indexOf(j));
                }
                line.getStops().remove(j);
            }
        }

        double objizdka = line.getStopsLength(line.getStopByIndex(indexOfFirstStopThatIsGood),line.getStopByIndex(indexOfLastStopThatIsGood));

        double actualPercent = 0;

        actualPercent = (objizdka / povodnaDlzka);
        for (Trip trip:line.getTrips()){
            LocalTime time1 = trip.getActualTimetable().get(indexOfFirstStopThatIsGood);
            LocalTime time2 = trip.getActualTimetable().get(indexOfLastStopThatIsGood);
            double previousTime = TimeExtender.minusLocalTime(time2,time1);
            double newTime = (TimeExtender.minusLocalTime(time2,time1) * actualPercent);

            if (previousTime<newTime){
                for (int k =indexOfLastStopThatIsGood; k< line.getStops().size();k++){
                    LocalTime tmp = trip.getActualTimetable().get(k);
                    trip.getActualTimetable().set(k,TimeExtender.plusLocalTime(tmp, (long) ((long) newTime-previousTime)));
                }
            }
            else{
                for (int k =indexOfLastStopThatIsGood; k< line.getStops().size();k++){
                    LocalTime tmp = trip.getActualTimetable().get(k);
                    trip.getActualTimetable().set(k,TimeExtender.minusLocalTime(tmp, (long) ((long) previousTime-newTime)));
                }
            }
        }

    }

    public void saveAndCloseClick(MouseEvent mouseEvent) {
        List<Street> newStreets = new ArrayList<>();
        ObservableList list = this.newRouteListView.getItems();
        if(list.isEmpty())
        {
            close();
            return;
        }
        for(Object streetId:list)
        {
            newStreets.add(this.streetMap.getStreet((String)streetId));
        }
        Street closedStreet = null;
        for(Street street:this.line.getStreets())
        {
            if(street.isClosed())
            {
                closedStreet=street;
                break;
            }
        }
        if(closedStreet==null)
        {
            close();
            return;
        }

        updateTimetable(this.line,newStreets,closedStreet);
        close();
    }

    private void close()
    {
        Stage stage = (Stage)this.newRouteListView.getScene().getWindow();
        stage.close();
    }
}
