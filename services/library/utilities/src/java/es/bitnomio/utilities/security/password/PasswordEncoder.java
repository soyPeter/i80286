package es.bitnomio.utilities.security.password;

import io.micronaut.core.annotation.Introspected;

import jakarta.validation.constraints.NotBlank;

@Introspected

public interface PasswordEncoder {

    String encode(@NotBlank String rawPassword);

    boolean matches(@NotBlank String rawPassword, @NotBlank String encodedPassword);

}
