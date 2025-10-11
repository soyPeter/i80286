package es.bitnomio.utilities.tracing.logbook;

import io.micronaut.context.annotation.Factory;
import io.micronaut.context.annotation.Value;
import io.micronaut.context.event.ApplicationEventPublisher;
import org.apache.commons.codec.digest.DigestUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zalando.logbook.Correlation;
import org.zalando.logbook.HttpLogWriter;
import org.zalando.logbook.Precorrelation;

import java.io.IOException;

@Factory
public class LogbookEventSink implements HttpLogWriter {

    private final Logger LOG = LoggerFactory.getLogger(LogbookEventSink.class.getName());

    @Override public void write(Precorrelation precorrelation, String request) throws IOException {
        var fingerPrint = DigestUtils.md5Hex(request);

        LOG.trace("audit: {}, fingerprint: {}", precorrelation.getId(), fingerPrint);

//        var auditEvent = new AuditEvent(precorrelation.getId(),
//            service,
//            precorrelation.getStart(),
//            null,
//            MDCUtils.getOptId(),
//            MDCUtils.getUsername(),
//            fingerPrint,
//            Timestamp.now(),
//            AuditEventType.REQUEST.name,
//            request);
//
//        applicationEventPublisher.publishEvent(auditEvent);
    }

    @Override public void write(Correlation correlation, String response) throws IOException {

        var fingerPrint = DigestUtils.md5Hex(response);

        LOG.trace("audit: {}, fingerprint: {}, request duration: {} ",
            correlation.getId(),
            fingerPrint,
            correlation.getDuration());

//        var auditEvent = new AuditEvent(correlation.getId(),
//            service,
//            correlation.getEnd(),
//            correlation.getDuration(),
//            MDCUtils.getOptId(),
//            MDCUtils.getUsername(),
//            fingerPrint,
//            Timestamp.now(),
//            AuditEventType.RESPONSE.name,
//            response);
//
//        applicationEventPublisher.publishEventAsync(auditEvent);
    }

}
