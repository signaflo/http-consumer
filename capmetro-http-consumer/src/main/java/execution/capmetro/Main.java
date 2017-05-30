package execution.capmetro;

import execution.ConsumerThreadController;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

/**
 * [Insert class description]
 *
 * @author Jacob Rachiele
 *         May. 16, 2017
 */
public class Main extends Application {

    public static void main(String... args) {
        Application.launch(args);
    }

    @Override
    public void start(Stage stage) {
        stage.setTitle("HTTP Consumer");
        Button btn = new Button("Start Execution");
        btn.setOnAction(event -> new Thread(new ConsumerThreadController()).start());
        StackPane root = new StackPane();
        root.getChildren().add(btn);
        stage.setScene(new Scene(root, 300, 250));
        stage.setOnCloseRequest(event -> System.exit(0));
        stage.show();
    }


}
