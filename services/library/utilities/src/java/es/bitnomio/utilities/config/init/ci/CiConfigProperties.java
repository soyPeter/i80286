package es.bitnomio.utilities.config.init.ci;

import io.micronaut.context.annotation.Value;
import jakarta.inject.Singleton;


@Singleton
public record CiConfigProperties(
        @Value("${CI_COMMIT_REF:`NOT_PRESENT`}")
        String ciCommitRef,

        @Value("${CI_COMMIT_SHA:`NOT_PRESENT`}")
        String ciCommitSha,

        @Value("${sentry.environment:`TEST`}")
        String sentryEnvironment
) {}
