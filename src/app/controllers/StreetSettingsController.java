package app.controllers;

import app.core.AlertHandler;
import app.models.maps.Line;
import app.models.maps.Street;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

public class StreetSettingsController {

    @FXML
    public TextField streetCoefficientTextField;

    @FXML
    public Button closedButton;

    private Street street;
    private Stage currentWindow;

    public void startUp(Street street, Stage currentWindow) {
        this.currentWindow=currentWindow;
        this.street=street;
        Platform.runLater(() -> {
            this.streetCoefficientTextField.setText(Integer.toString(street.getTrafficCoefficient()));
        });
        guiRefresh();
    }

    private void guiRefresh()
    {
        Platform.runLater(() -> {
            if(this.street.isClosed())
            {
                this.closedButton.textProperty().setValue("Open street");
            }else {
                this.closedButton.textProperty().setValue("Close street");
            }
        });

    }

    public void closeClick()
    {
        if(this.street.isClosed())
        {
            this.street.setClosed(false);
        }else {
            this.street.setClosed(true);
            for(Line line:this.street.getLines())
            {
                line.computeConflicts();
            }
        }
        guiRefresh();
    }

    public void okClick()
    {
        try {
            this.street.setTrafficCoefficient(Integer.parseInt(streetCoefficientTextField.textProperty().get()));
            cancelClick();
        } catch (Exception e) {
            AlertHandler.showError(e);
        }
    }

    public void cancelClick() {
        Stage stage = (Stage) this.closedButton.getScene().getWindow();
        stage.close();
        this.currentWindow.show();
    }
}
