package es.bitnomio.utilities.config.init;

import es.bitnomio.utilities.config.init.app.AppConfigProperties;
import es.bitnomio.utilities.utils.MDCUtils;
import io.micronaut.discovery.event.ServiceReadyEvent;
import io.micronaut.runtime.event.annotation.EventListener;
import org.slf4j.Logger;

import jakarta.inject.Singleton;

@Singleton
public class AppServiceReadyEventListener {

    private static final Logger log = org.slf4j.LoggerFactory.getLogger(AppServiceReadyEventListener.class);
    private final AppConfigProperties appConfigProperties;

    public AppServiceReadyEventListener(AppConfigProperties appConfigProperties) {
        this.appConfigProperties = appConfigProperties;
    }

    @EventListener
    public void onServiceReadyEvent(final ServiceReadyEvent serviceReadyEvent) {

        log.info("Setting init config after service ready");

        System.setProperty("application.name", appConfigProperties.name());
        MDCUtils.setServiceName(appConfigProperties.name());
    }
}
