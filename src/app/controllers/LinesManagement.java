package app.controllers;

import app.components.LineTableItem;
import app.models.TimeExtender;
import app.models.maps.Line;
import app.models.maps.Street;
import app.models.maps.StreetMap;
import javafx.application.Platform;
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

/**
 * Show conflict lines in table.
 * @author Petr Křehlík, Martin Klobušický
 * @date 13.5.2020
 */
public class LinesManagement {
    public TableView linesTableView;

    private List<Line> lines;
    private StreetMap streetMap;

    /**
     * Initialize object and GUI.
     * @param lines List of all lines.
     * @param streetMap Full street map.
     */
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

    /**
     * Print all lines with at least one conflict (closed street).
     */
    private void refreshGui()
    {
        Platform.runLater(() -> {
            this.linesTableView.getItems().clear();
            List<Line> lines = badLines();
            for(Line line:lines)
            {
                this.linesTableView.getItems().add(new LineTableItem(line.getId(), Integer.toString(line.getConflictsCount()),line));
            }
        });

    }

    private int clickCounter=0;
    private LocalTime previous=LocalTime.now();

    /**
     * Open specific line window.
     */
    public void openLine()
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
            try {
                ((LineManagement)fxmlLoader.getController()).startUp(item.getLine(),this.streetMap);
                Stage stage = new Stage();
                stage.setTitle("Line manager");
                stage.setScene(scene);
                stage.initOwner(currentWindow);
                stage.initModality(Modality.APPLICATION_MODAL);
                stage.showAndWait();
            } catch (Exception e) {
                e.printStackTrace();
            }

            refreshGui();
        }
        clickCounter=0;

    }

    /**
     * Find lines with conflicts.
     * @return List of lines that has conflicts.
     */
    private List<Line> badLines(){
        List<Line> tmp = new ArrayList<>();
        for (Line line:lines){
            if (line.isConflict()){
                tmp.add(line);
            }
        }
        return tmp;
    }




}
