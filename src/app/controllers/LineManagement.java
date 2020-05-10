package app.controllers;

import app.components.LineTableItem;
import app.models.maps.Line;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.util.List;

public class LineManagement {
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
            this.linesTableView.getItems().add(new LineTableItem(line.getId(),"NO"));
        }
    }

}
