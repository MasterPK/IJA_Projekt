package app;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.shape.Line;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        FXMLLoader root = new FXMLLoader(getClass().getResource("main.fxml"));

        primaryStage.setTitle("Public Transport Map");
        primaryStage.setScene(new Scene(root.load(), 300, 275));
        primaryStage.show();


        Controller controller = root.getController();
        controller.drawMap();

    }


    public static void main(String[] args) {
        launch(args);
    }
}
