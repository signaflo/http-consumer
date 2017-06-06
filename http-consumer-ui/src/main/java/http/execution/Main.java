package http.execution;

import data.model.PathPropertiesData;
import http.HttpDailyRunnerView;
import http.URLView;
import javafx.application.Application;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.TilePane;
import javafx.stage.Stage;

import java.io.File;

/**
 * [Insert class description]
 *
 * @author Jacob Rachiele
 *         May. 30, 2017
 */
public final class Main extends Application {

    @Override
    public void start(Stage stage) {
        PathPropertiesData data = new PathPropertiesData();
        Node urlView = new URLView();
        GridPane root = new GridPane();
        root.add(urlView, 0, 0);
        Scene scene = new Scene(root);
        String slash = File.separator;
        scene.getStylesheets().add(slash + "url.css");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String... args) {
        Application.launch(args);
    }
}
