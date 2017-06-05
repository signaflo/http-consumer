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

/**
 * [Insert class description]
 *
 * @author Jacob Rachiele
 *         May. 30, 2017
 */
public class Main extends Application {

    @Override
    public void start(Stage stage) {
        PathPropertiesData data = new PathPropertiesData();
        Node urlView = new URLView(10);
        GridPane root = new GridPane();
        root.add(urlView, 0, 0);
        Scene scene = new Scene(root, 600, 400);
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String... args) {
        Application.launch(args);
    }
}
