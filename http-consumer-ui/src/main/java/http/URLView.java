package http;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ChoiceBox;
import javafx.scene.layout.VBox;
import javafx.util.StringConverter;

/**
 * [Insert class description]
 *
 * @author Jacob Rachiele
 *         Jun. 05, 2017
 */
public class URLView extends VBox {

    public URLView(int padding) {
        super(padding);
        this.getChildren().add(getChoiceBox());
    }

    ChoiceBox<StringProperty> getChoiceBox() {
        ObservableList<StringProperty> properties = FXCollections.observableArrayList();
        StringProperty httpDefault = new SimpleStringProperty("HTTP");
        properties.add(new SimpleStringProperty("FTP"));
        properties.add(httpDefault);
        properties.add(new SimpleStringProperty("HTTPS"));
        ChoiceBox<StringProperty> choiceBox = new ChoiceBox<>(properties);
        choiceBox.setValue(httpDefault);
        choiceBox.setConverter(getConverter());
        return choiceBox;
    }

    StringConverter<StringProperty> getConverter() {
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
