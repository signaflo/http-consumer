package http.data;

import java.io.InputStream;

public interface Source {

    InputStream read();

    int size();
}
