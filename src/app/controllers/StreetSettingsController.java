package app.controllers;

import app.models.maps.Street;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

public class StreetSettingsController extends BaseController {

    @FXML
    public TextField streetCoefficientTextField;

    @FXML
    public Button closedButton;

    private Street street;

    public StreetSettingsController(Street street) {
        this.street=street;
    }

    /**
     * Function that is called on Scene start up.
     */
    @Override
    public void startUp() {
        this.streetCoefficientTextField.setText(Integer.toString(street.getTrafficCoefficient()));
        if(this.street.isClosed())
        {
            this.closedButton.textProperty().setValue("Closed");
        }

    }

    /**
     * Function that is called on Scene close.
     */
    @Override
    public void close() {

    }
}
