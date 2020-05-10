package app.controllers;

import app.components.LineTableItem;
import app.models.maps.Line;
import javafx.event.Event;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseButton;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.awt.event.MouseEvent;
import java.io.IOException;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class LinesManagement {
    public TableView linesTableView;

    private List<Line> lines;

    public void startUp(List<Line> lines)
    {
        this.lines=lines;

        TableColumn routeId = new TableColumn("Line");
        routeId.setCellValueFactory(new PropertyValueFactory<>("routeId"));

        TableColumn conflict = new TableColumn("Conflict");
        conflict.setCellValueFactory(new PropertyValueFactory<>("conflict"));

        this.linesTableView.getColumns().addAll(routeId, conflict);

        refreshGui();
    }

    private void refreshGui()
    {

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

        if(minusLocalTime(LocalTime.now(),previous)>1)
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
            ((LineManagement)fxmlLoader.getController()).startUp(item.getLine());
            Stage stage = new Stage();
            stage.setTitle("Line manager");
            stage.setScene(scene);
            stage.initOwner(currentWindow);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();
        }
        clickCounter=0;

    }

    private int minusLocalTime(LocalTime diff1, LocalTime diff2) {
        LocalTime diff = diff1.minusHours(diff2.getHour())
                .minusMinutes(diff2.getMinute())
                .minusSeconds(diff2.getSecond());

        return (diff.getHour() * 60 * 60) + (diff.getMinute() * 60) + (diff.getSecond());
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
