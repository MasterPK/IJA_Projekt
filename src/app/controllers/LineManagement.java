package app.controllers;

import app.core.AlertHandler;
import app.models.TimeExtender;
import app.models.maps.Coordinate;
import app.models.maps.Line;
import app.models.maps.Street;
import app.models.maps.StreetMap;
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
                this.selectStreetListView.getItems().add(street.getId());
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
                    this.selectStreetListView.getItems().add(street.getId());
                }
            });
        }

    }
    public void updateTimetable(Line line, List<Street> newStreets, Street closedStreet){

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
