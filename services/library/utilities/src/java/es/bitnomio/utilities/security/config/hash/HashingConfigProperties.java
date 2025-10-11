package es.bitnomio.utilities.security.config.hash;

import io.micronaut.context.annotation.ConfigurationProperties;
import io.micronaut.core.annotation.Nullable;

@ConfigurationProperties("app.security.hash")
public record HashingConfigProperties (@Nullable String salt) { }
