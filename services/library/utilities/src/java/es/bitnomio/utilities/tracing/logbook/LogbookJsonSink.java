package es.bitnomio.utilities.tracing.logbook;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.micronaut.context.annotation.Factory;
import org.zalando.logbook.HttpLogFormatter;
import org.zalando.logbook.Sink;
import org.zalando.logbook.json.JsonHttpLogFormatter;
import org.zalando.logbook.logstash.LogstashLogbackSink;

import jakarta.inject.Singleton;

@Factory
public class LogbookJsonSink {

  private final ObjectMapper mapper;

  public LogbookJsonSink(ObjectMapper mapper) {
    this.mapper = mapper;
  }

  @Singleton
  public Sink sink() {
    HttpLogFormatter formatter = new JsonHttpLogFormatter(mapper);
    return new LogstashLogbackSink(formatter);
  }

}
