package net.alexhyisen.pathfinder.gui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainFX extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        var loader = new FXMLLoader(getClass().getResource("/fxml/main.fxml"));
        var scene = new Scene(loader.load());
        primaryStage.setScene(scene);
        MainController controller = loader.getController();
        primaryStage.setOnCloseRequest(v->controller.shutdown());
        primaryStage.setTitle("pathfinder");
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}
