package http.execution;

import http.data.model.PathPropertiesData;
import http.data.view.PathPropertiesView;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;

/**
 * [Insert class description]
 *
 * @author Jacob Rachiele
 *         May. 30, 2017
 */
public class HttpDailyRunnerView extends GridPane {

    private final GridPane pathPropertiesView;

    HttpDailyRunnerView(PathPropertiesData data) {
        Button submitTask = new Button("Submit Task");
        this.pathPropertiesView = new PathPropertiesView(data);
        this.add(pathPropertiesView, 0, 0);
    }
}
