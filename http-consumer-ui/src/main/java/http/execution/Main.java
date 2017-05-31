package http.execution;

import http.data.model.PathPropertiesData;
import http.data.view.PathPropertiesView;
import javafx.application.Application;
import javafx.beans.property.SimpleStringProperty;
import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.geometry.VPos;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Screen;
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
        HBox hbox = new HBox();
        Text text = new Text();
        text.textProperty().bind(data.getPathPropertiesString());
        hbox.getChildren().add(text);
        VBox root = new VBox(10, pathPropertiesView, hbox);
        Scene scene = new Scene(root, 600, 400);
        text.setLayoutX(-text.getLayoutBounds().getMinX());
        text.setLayoutY(-text.getLayoutBounds().getMinY());
        System.out.println(text.getLayoutBounds().getMaxY());
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String... args) {
        Application.launch(args);
    }
}
