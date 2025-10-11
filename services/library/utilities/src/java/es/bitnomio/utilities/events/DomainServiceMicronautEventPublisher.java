package es.bitnomio.utilities.events;


import io.micronaut.context.event.ApplicationEventPublisher;
import jakarta.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@Singleton
public class DomainServiceMicronautEventPublisher implements DomainServiceEventPublisher {

    private final static Logger LOG = LoggerFactory.getLogger(DomainServiceMicronautEventPublisher.class.getName());

    private final ApplicationEventPublisher<Object> eventPublisher;

    public DomainServiceMicronautEventPublisher(ApplicationEventPublisher<Object> eventPublisher) {
        this.eventPublisher = eventPublisher;
    }

    /**
     * <p>
     *
     * <h3>Events</h3>
     * <p>
     * At this moment, this implementation is intended to check if application events worth the extra plumbing and time.
     *
     * <h3><a href="https://docs.micronaut.io/latest/guide/index.html#contextEvents"> Micronaut events</a></h3>
     * <p>
     * Publishing an event is synchronous by default! The publishEvent method will not return until all listeners have been executed.
     * Move this work off to a thread pool if it is time intensive.
     *
     * <strong>This class should be revisited in order to determine if we can keep this or should implements a RabbitMQ message queue</strong>
     * </p>
     * <p>
     * 2024 [Peter]
     **/
    @Override public void publish(Object event) {
        eventPublisher.publishEvent(event);
    }


    /**
     * Default async context publication
     *
     * @param event the context event to publish
     */
    @Override public void publishAsync(Object event) {

        publishAsync(event, 300, TimeUnit.MILLISECONDS);

    }

    /**
     *
     * @param event event object
     * @param timeOut time out, default 300
     * @param unit unit for the timeout, default milliseconds
     */
    @Override public void publishAsync(Object event, long timeOut, TimeUnit unit) {
        try {
            eventPublisher.publishEventAsync(event).get(timeOut, unit);
        }
        catch (ExecutionException | InterruptedException | TimeoutException e) {
            LOG.error(e.getMessage());
        }
    }


}
