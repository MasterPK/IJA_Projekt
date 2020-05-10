package app.controllers;

import app.components.LineTableItem;
import app.models.TimeExtender;
import app.models.maps.Line;
import app.models.maps.StreetMap;
import javafx.event.Event;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class LinesManagement {
    public TableView linesTableView;

    private List<Line> lines;
    private StreetMap streetMap;

    public void startUp(List<Line> lines, StreetMap streetMap)
    {
        this.lines=lines;
        this.streetMap=streetMap;

        TableColumn routeId = new TableColumn("Line");
        routeId.setCellValueFactory(new PropertyValueFactory<>("routeId"));

        TableColumn conflict = new TableColumn("Conflict");
        conflict.setCellValueFactory(new PropertyValueFactory<>("conflict"));

        this.linesTableView.getColumns().addAll(routeId, conflict);

        refreshGui();
    }

    private void refreshGui()
    {
        List<Line> lines = badLines();
        for(Line line:lines)
        {
            this.linesTableView.getItems().add(new LineTableItem(line.getId(), line.isConflict() ? "YES" : "NO",line));
        }
    }

    private int clickCounter=0;
    private LocalTime previous=LocalTime.now();
    public void openLine(Event event)
    {
        if(this.linesTableView.getSelectionModel().isEmpty())
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

        if(clickCounter == 2 ){
            LineTableItem item = (LineTableItem)this.linesTableView.getSelectionModel().getSelectedItem();
            Stage currentWindow = (Stage) this.linesTableView.getScene().getWindow();

            FXMLLoader fxmlLoader = new FXMLLoader();
            fxmlLoader.setLocation(getClass().getResource("/app/fxml/lineManagement.fxml"));
            Scene scene = null;
            try {
                scene = new Scene(fxmlLoader.load(), 800, 500);
            } catch (IOException e) {
                e.printStackTrace();
                return;
            }
            ((LineManagement)fxmlLoader.getController()).startUp(item.getLine(),this.streetMap);
            Stage stage = new Stage();
            stage.setTitle("Line manager");
            stage.setScene(scene);
            stage.initOwner(currentWindow);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();
        }
        clickCounter=0;

    }



    private List<Line> badLines(){
        List<Line> tmp = new ArrayList<>();
        for (Line line:lines){
            if (line.getConflict() == true){
                tmp.add(line);
            }
        }
        return tmp;
    }

}
