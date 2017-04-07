package execution;

import data.FileSink;
import data.HttpSource;
import data.Sink;
import data.UpdatingSource;
import lombok.NonNull;

import java.nio.charset.Charset;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

/**
 * A consumer of data from a HTTP source.
 *
 * @author Jacob Rachiele
 *         Feb. 23, 2017
 */
public final class HttpRunner implements Runnable {

    private static final DateTimeFormatter DTF = DateTimeFormatter.ISO_LOCAL_DATE;

    private final UpdatingSource source;

    HttpRunner(@NonNull String address) {
        this.source = new HttpSource(address);
    }

    @Override
    public void run() {
        this.source.update();
        if (this.source.fresh()) {
            String outputPath = "data/" + LocalDateTime.now().format(DTF);
            String fileName = LocalTime.now().toString();
            Sink sink = new FileSink(this.source.size(), outputPath, fileName,
                                     this.source.read(), Charset.forName("UTF-8"));
            sink.write();
        }
    }
}
