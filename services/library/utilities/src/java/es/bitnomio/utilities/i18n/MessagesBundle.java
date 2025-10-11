package es.bitnomio.utilities.i18n;

import io.micronaut.context.i18n.ResourceBundleMessageSource;

import jakarta.inject.Singleton;

@Singleton
public class MessagesBundle extends ResourceBundleMessageSource {

    public MessagesBundle() {
        super("i18n.messages");
    }

}

