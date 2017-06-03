package data;

import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * A description of a file path.
 */
@EqualsAndHashCode @ToString
public class PathProperties {

    private final String prefix;
    private final String suffix;
    private final String directory;

    public PathProperties(String prefix, String suffix, String directory) {
        this.prefix = prefix;
        this.suffix = suffix;
        this.directory = directory;
    }

    public String getPrefix() {
        return this.prefix;
    }

    public String getSuffix() {
        return this.suffix;
    }

    public String getDirectory() {
        return this.directory;
    }
}
