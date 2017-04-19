package http.execution;

import http.data.*;
import lombok.NonNull;

import java.nio.charset.Charset;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

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
    private final ContentType contentType;

    public HttpDailyRunner(@NonNull String address, @NonNull String directory, @NonNull String filePrefix,
                           @NonNull String fileSuffix, @NonNull Map<String, String> requestProperties,
                           @NonNull ContentType contentType) {
        this.source = new HttpSource(address, requestProperties);
        this.directory = directory;
        this.filePrefix = filePrefix;
        this.fileSuffix = fileSuffix;
        this.contentType = contentType;
    }

    @Override
    public void run() {
        this.source.update();
        if (this.source.fresh()) {
            String outputPath = directory + "/" + LocalDateTime.now().format(DTF);
            String fileName = filePrefix + "-" + LocalTime.now().toString() + "." + fileSuffix;
            Sink sink;
            if (contentType == ContentType.TEXT) {
                sink = new FileSink(this.source.size(), outputPath, fileName, this.source.read(), Charset.forName
                        ("UTF-8"));
            } else {
                sink = new BinarySink(this.source.size(), outputPath, fileName, this.source.read());
            }
            sink.write();
        }
    }
}
