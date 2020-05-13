package app.controllers;

import app.core.AlertHandler;
import app.models.maps.Line;
import app.models.maps.Street;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

/**
 * Controller to street settings window.
 * @author Petr Křehlík, Martin Klobušický
 * @date 13.5.2020
 */
public class StreetSettingsController {

    @FXML
    public TextField streetCoefficientTextField;

    @FXML
    public Button closedButton;

    private Street street;
    private Stage currentWindow;

    /**
     * Initialize object. Load street coefficient and show it.
     * @param street Street.
     * @param currentWindow Parent window.
     */
    public void startUp(Street street, Stage currentWindow) {
        this.currentWindow = currentWindow;
        this.street = street;
        Platform.runLater(() -> {
            this.streetCoefficientTextField.setText(Integer.toString(street.getTrafficCoefficient()));
        });
        guiRefresh();
    }

    /**
     * Set correctly open/close street button.
     */
    private void guiRefresh() {
        Platform.runLater(() -> {
            if (this.street.isClosed()) {
                this.closedButton.textProperty().setValue("Open street");
            } else {
                this.closedButton.textProperty().setValue("Close street");
            }
        });

    }

    /**
     * On start/stop button click.
     * Compute new conflicts.
     */
    public void closeClick() {
        if (this.street.isClosed()) {
            this.street.setClosed(false);
            for (Line line : this.street.getLines()) {
                line.restoreBackUp();
                line.computeConflicts();
            }
        } else {
            this.street.setClosed(true);
            for (Line line : this.street.getLines()) {
                line.restoreBackUp();
                line.computeConflicts();
            }

        }
        guiRefresh();
    }

    /**
     * On window close button click.
     * Set street color based on if it is open/closed.
     * Close window.
     */
    public void okClick() {
        try {
            this.street.setTrafficCoefficient(Integer.parseInt(streetCoefficientTextField.textProperty().get()));
            Platform.runLater(() -> {
                if(!street.isClosed())
                {
                    if(street.getTrafficCoefficient()>1)
                    {
                        street.getGui().setStyle("-fx-stroke: orange; -fx-stroke-width: 2;");
                    }
                    else {
                        street.getGui().setStyle("-fx-stroke: black; -fx-stroke-width: 2;");
                    }
                }

            });

            cancelClick();
        } catch (Exception e) {
            AlertHandler.showError(e);
        }
    }

    /**
     * Close window.
     */
    public void cancelClick() {
        Stage stage = (Stage) this.closedButton.getScene().getWindow();
        stage.close();
        this.currentWindow.show();
    }
}
