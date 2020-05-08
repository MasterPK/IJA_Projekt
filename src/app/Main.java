package app;

import app.controllers.Controller;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    private Controller controller;

    @Override
    public void start(Stage primaryStage) throws Exception{
        FXMLLoader root = new FXMLLoader(getClass().getResource("view/main.fxml"));


        primaryStage.setTitle("Public Transport Map");
        primaryStage.setScene(new Scene(root.load()));

        this.controller = root.getController();
        controller.startUp();

        primaryStage.setMaximized(true);
        primaryStage.show();



    }

    /**
     * This method is called when the application should stop, and provides a
     * convenient place to prepare for application exit and destroy resources.
     *
     * <p>
     * The implementation of this method provided by the Application class does nothing.
     * </p>
     *
     * <p>
     * NOTE: This method is called on the JavaFX Application Thread.
     * </p>
     */
    @Override
    public void stop() throws Exception {
        this.controller.close();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
