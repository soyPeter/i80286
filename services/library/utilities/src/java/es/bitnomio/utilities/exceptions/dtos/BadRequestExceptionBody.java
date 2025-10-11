package es.bitnomio.utilities.exceptions.dtos;

import io.micronaut.core.annotation.Introspected;

import java.util.Map;
import java.util.Set;

@Introspected
public record BadRequestExceptionBody(Map<String, Set<String>> errors) {

    public static final String CODE = "BAD_REQUEST_EXCEPTION";
    public static final String MESSAGE = "Invalid request payload";

}
