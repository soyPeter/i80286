package es.bitnomio.utilities.config.security.encryption;

import io.micronaut.context.annotation.ConfigurationProperties;

@ConfigurationProperties("app.security.encryption")
public record EncryptionConfigProperties(String key, String ivParameter) {}
