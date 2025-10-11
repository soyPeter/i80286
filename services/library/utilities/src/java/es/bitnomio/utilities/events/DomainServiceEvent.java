package es.bitnomio.utilities.events;

import es.bitnomio.utilities.utils.MDCUtils;

import java.time.Instant;
import java.util.Objects;

public record DomainServiceEvent(
        String id,
        String username,
        Instant instant,
        String code
) {

    public DomainServiceEvent {

        if (Objects.isNull(id)) {
            id = MDCUtils.getRequestId();
        }

        if (Objects.isNull(username)) {
            username = MDCUtils.getUser();
        }

        if (Objects.isNull(instant)) {
            instant = Instant.now();
        }
    }

    public DomainServiceEvent(String eventCode) {
        this(null, null, null, eventCode);
    }
}
