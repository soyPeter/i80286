package es.bitnomio.utilities.events;

import java.util.concurrent.TimeUnit;

public interface DomainServiceEventPublisher {

    void publish(Object event);

    void publishAsync(Object event);

    void publishAsync(Object event, long timeOut, TimeUnit unit);

}
