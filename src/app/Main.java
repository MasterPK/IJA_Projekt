package app;

import app.controllers.Controller;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        FXMLLoader root = new FXMLLoader(getClass().getResource("main.fxml"));


        primaryStage.setTitle("Public Transport Map");
        primaryStage.setScene(new Scene(root.load()));
        primaryStage.setMaximized(true);
        primaryStage.show();

        Controller controller = root.getController();
        controller.startUp();

        controller.drawMap();


    }


    public static void main(String[] args) {
        launch(args);
    }
}
