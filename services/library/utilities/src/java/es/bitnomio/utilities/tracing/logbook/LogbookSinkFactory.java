package es.bitnomio.utilities.tracing.logbook;

import io.micronaut.context.annotation.Value;
import org.zalando.logbook.DefaultHttpLogWriter;
import org.zalando.logbook.DefaultSink;
import org.zalando.logbook.Sink;
import org.zalando.logbook.json.JsonHttpLogFormatter;

import jakarta.inject.Singleton;

@Singleton
public class LogbookSinkFactory {

    private final LogbookEventSink logbookEventSink;

    @Value("${app.tracing.debug:false}")
    private boolean isTracingDebugEnabled;

    @Value("${app.tracing.events.enabled:false}")
    private boolean isTracingEventEnabled;

    public LogbookSinkFactory(LogbookEventSink logbookEventSink) {
        this.logbookEventSink = logbookEventSink;
    }

    Sink getSink() {
        if (isTracingDebugEnabled || !isTracingEventEnabled) {
            return new DefaultSink(
                    new JsonHttpLogFormatter(),
                    new DefaultHttpLogWriter()
            );
        } else {
            return new DefaultSink(
                    new JsonHttpLogFormatter(),
                    logbookEventSink);
        }
    }


}
