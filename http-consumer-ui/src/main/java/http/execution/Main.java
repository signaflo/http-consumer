package http.execution;

import http.data.model.PathPropertiesData;
import http.data.view.PathPropertiesView;
import javafx.application.Application;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.awt.*;

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
        Node pathPropertiesView = new PathPropertiesView(data);
        Text text = new Text();
        text.textProperty().bind(new SimpleStringProperty(System.getProperty("user.dir")));
        VBox vBox = new VBox(10);
        vBox.getChildren().addAll(pathPropertiesView, text);
        Scene scene = new Scene(vBox, 300, 250);
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String... args) {
        Application.launch(args);
    }
}
