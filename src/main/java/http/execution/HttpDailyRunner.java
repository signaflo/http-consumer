package http.execution;

import http.data.FileSink;
import http.data.HttpSource;
import http.data.Sink;
import http.data.UpdatingSource;
import lombok.NonNull;

import java.nio.charset.Charset;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

/**
 * A consumer of http.data from a HTTP source.
 *
 * @author Jacob Rachiele
 *         Feb. 23, 2017
 */
public final class HttpDailyRunner implements Runnable {

    private static final DateTimeFormatter DTF = DateTimeFormatter.ISO_LOCAL_DATE;

    private final UpdatingSource source;
    private final String directory;
    private final String filePrefix;
    private final String fileSuffix;

    public HttpDailyRunner(@NonNull String address, @NonNull String directory, @NonNull String filePrefix,
                    @NonNull String fileSuffix, @NonNull String contentType) {
        this.source = new HttpSource(address);
        this.directory = directory;
        this.filePrefix = filePrefix;
        this.fileSuffix = fileSuffix;
    }

    @Override
    public void run() {
        this.source.update();
        if (this.source.fresh()) {
            String outputPath = directory + "/" + LocalDateTime.now().format(DTF);
            String fileName = filePrefix + "-" + LocalTime.now().toString() + "." + fileSuffix;
            Sink sink = new FileSink(this.source.size(), outputPath, fileName,
                                     this.source.read(), Charset.forName("UTF-8"));
            sink.write();
        }
    }
}
