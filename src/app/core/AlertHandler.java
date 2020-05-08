package app.core;

import javafx.scene.control.Alert;

public abstract class AlertHandler {
    public static void showError(Exception e)
    {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText("An error occurred!");
        alert.setContentText(e.getMessage());

        alert.showAndWait();
    }
    public static void showWarning(Exception e)
    {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Warning");
        alert.setHeaderText("Warning: A non-fatal error occurred!");
        alert.setContentText(e.getMessage()+"\n\nYou can continue but be careful with the next steps!");

        alert.showAndWait();
    }

}
