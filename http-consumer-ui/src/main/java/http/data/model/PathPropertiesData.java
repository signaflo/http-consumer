package http.data.model;

import data.PathProperties;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * [Insert class description]
 *
 * @author Jacob Rachiele
 *         May. 30, 2017
 */
public class PathPropertiesData {

    private PathProperties pathProperties = new PathProperties("trips", "json", "data");
    private final StringProperty pathPropertiesString = new SimpleStringProperty(pathProperties.toString());

    public PathPropertiesData() {
    }

    public final void createPathProperties(String prefix, String suffix, String directory) {
        this.pathProperties = new PathProperties(prefix, suffix, directory);
        this.pathPropertiesString.setValue(this.pathProperties.toString());
    }

    public final PathProperties getPathProperties() {
        return this.pathProperties;
    }

    public final StringProperty getPathPropertiesString() {
        return this.pathPropertiesString;
    }


}
