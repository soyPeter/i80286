package es.bitnomio.utilities.exceptions.dtos;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import es.bitnomio.utilities.utils.MDCUtils;
import io.micronaut.core.annotation.Introspected;
import io.micronaut.core.annotation.Nullable;


import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;


@JsonInclude(JsonInclude.Include.NON_ABSENT)
@JsonPropertyOrder({"requestId", "code", "message", "metadata"})
@Introspected
public record HttpRestExceptionBody(

    @JsonProperty("code")
    String code,

    @JsonProperty("message")
    String message,

    @JsonProperty("requestId")
    @Nullable String requestId,

    @JsonProperty("metadata")
    @Nullable List<Object> metadata,

    @JsonProperty("errors")
    @Nullable Map<String, Set<String>> errors

) implements Serializable {

  public HttpRestExceptionBody {
    if (Objects.isNull(requestId)) {
      requestId = MDCUtils.getRequestId();
    }
  }

  public HttpRestExceptionBody(String code, String message) {
    this(code, message, null, null, null);
  }

  public HttpRestExceptionBody(String code, String message, String operationId) {
    this(code, message, operationId, null, null);
  }

  public HttpRestExceptionBody(String code, String message, Map<String, Set<String>> errors) {
    this(code, message, null, null, errors);
  }

}
