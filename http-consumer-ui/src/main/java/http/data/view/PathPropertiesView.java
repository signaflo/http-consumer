package http.data.view;

import http.data.model.PathPropertiesData;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.control.TextField;

/**
 * [Insert class description]
 *
 * @author Jacob Rachiele
 *         May. 30, 2017
 */
public class PathPropertiesView extends GridPane {

    PathPropertiesData pathProperties;

    Label prefixLabel = new Label("File Prefix");
    Label suffixLabel = new Label("File Suffix");
    Label directoryLabel = new Label("Directory");

    TextField prefix = new TextField();
    TextField suffix = new TextField();
    TextField directory = new TextField();

    Button submit = new Button("Submit");

    public PathPropertiesView(PathPropertiesData data) {
        this.pathProperties = data;

        this.submit.setOnAction(
                e -> data.createPathProperties(prefix.getText(), suffix.getText(), directory.getText()));

        this.setHgap(5.0);
        this.setVgap(5.0);

        this.add(prefixLabel, 1, 1);
        this.add(suffixLabel, 1, 2);
        this.add(directoryLabel, 1, 3);

        this.add(prefix, 2, 1);
        this.add(suffix, 2, 2);
        this.add(directory, 2, 3);

        this.add(submit, 1, 4);
    }

}
