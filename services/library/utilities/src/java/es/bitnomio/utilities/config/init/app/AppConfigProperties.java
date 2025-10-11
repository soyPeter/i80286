package es.bitnomio.utilities.config.init.app;

import io.micronaut.context.annotation.ConfigurationProperties;

@ConfigurationProperties("micronaut.application")
public record AppConfigProperties(String name) {
}
