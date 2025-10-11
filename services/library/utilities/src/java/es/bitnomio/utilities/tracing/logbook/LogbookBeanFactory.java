package es.bitnomio.utilities.tracing.logbook;

import io.micronaut.context.annotation.Factory;
import org.zalando.logbook.Logbook;
import org.zalando.logbook.json.JsonBodyFilters;

import jakarta.inject.Singleton;
import java.util.Set;

import static org.zalando.logbook.core.Conditions.contentType;
import static org.zalando.logbook.core.Conditions.exclude;
import static org.zalando.logbook.core.Conditions.requestTo;
import static org.zalando.logbook.core.HeaderFilters.authorization;
import static org.zalando.logbook.core.QueryFilters.accessToken;
import static org.zalando.logbook.core.QueryFilters.replaceQuery;

@Factory
public class LogbookBeanFactory {

    private final LogbookSinkFactory logbookSinkFactory;

    public LogbookBeanFactory(LogbookSinkFactory logbookSinkFactory) {
        this.logbookSinkFactory = logbookSinkFactory;
    }

    @Singleton
    Logbook logbook() {

        return Logbook.builder()
                .condition(
                        exclude(
                                requestTo("/health"),
                                requestTo("/health-check"),
                                contentType("application/octet-stream")
                        )
                )
                .bodyFilter(JsonBodyFilters.replaceJsonStringProperty(Set.of("password", "pass"), "<secret>"))
                .queryFilter(accessToken())
                .queryFilter(replaceQuery("password", "<secret>"))
                .queryFilter(replaceQuery("pass", "<secret>"))
                .headerFilter(authorization())
                .sink(logbookSinkFactory.getSink())
                .build();
    }

}
