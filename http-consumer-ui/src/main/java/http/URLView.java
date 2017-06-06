package http;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.util.StringConverter;

/**
 * [Insert class description]
 *
 * @author Jacob Rachiele
 *         Jun. 05, 2017
 */
public class URLView extends GridPane {

    public URLView() {
        super();
        this.setHgap(20);
        this.setVgap(10);
        ChoiceBox<StringProperty> choiceBox = getProtocolChoiceBox();
        Label protocolLabel = getProtocolLabel();
        protocolLabel.setLabelFor(choiceBox);
        this.add(protocolLabel, 0, 0);
        this.add(choiceBox, 1, 0);
        this.add(getHostLabel(), 0, 1);
        this.add(getHostTextField(), 1, 1);
        this.add(getPortLabel(), 0, 2);
        this.add(getPortTextField(), 1, 2);
        this.add(getFileLabel(), 0, 3);
        this.add(getFileTextField(), 1, 3);
    }

    private static Label getProtocolLabel() {
        Label protocolLabel = new Label("Protocol");
        protocolLabel.setFont(Font.font("SanSerif", FontWeight.BOLD, 14));
        return protocolLabel;
    }

    private static Label getHostLabel() {
        Label hostLabel = new Label("Host");
        hostLabel.setFont(Font.font("SanSerif", FontWeight.BOLD, 14));
        return hostLabel;
    }

    private static Label getPortLabel() {
        Label portLabel = new Label("Port");
        //TODO: validate port range.
        portLabel.setFont(Font.font("Sans Serif", FontWeight.BOLD, 14));
        return portLabel;
    }

    private static Label getFileLabel() {
        Label fileLabel = new Label("File");
        fileLabel.setFont(Font.font("Sans Serif", FontWeight.BOLD, 14));
        return fileLabel;
    }

    private static ChoiceBox<StringProperty> getProtocolChoiceBox() {
        ObservableList<StringProperty> properties = FXCollections.observableArrayList();
        StringProperty httpDefault = new SimpleStringProperty("HTTP");
        properties.add(new SimpleStringProperty("FTP"));
        properties.add(httpDefault);
        properties.add(new SimpleStringProperty("HTTPS"));
        ChoiceBox<StringProperty> choiceBox = new ChoiceBox<>(properties);
        choiceBox.setValue(httpDefault);
        choiceBox.setConverter(stringConverter());
        return choiceBox;
    }

    private static TextField getHostTextField() {
        TextField hostField = new TextField();
        hostField.setPromptText("host location");
        return hostField;
    }

    private static TextField getPortTextField() {
        TextField portField = new TextField();
        portField.setText("8081");
        portField.setPromptText("port number");
        return portField;
    }

    private static TextField getFileTextField() {
        TextField fileTextField = new TextField();
        fileTextField.setPromptText("file path");
        return fileTextField;
    }

    private static StringConverter<StringProperty> stringConverter() {
        return new StringConverter<StringProperty>() {
            @Override
            public String toString(StringProperty object) {
                return object.getValue();
            }

            @Override
            public StringProperty fromString(String string) {
                return new SimpleStringProperty(string);
            }
        };
    }
}
