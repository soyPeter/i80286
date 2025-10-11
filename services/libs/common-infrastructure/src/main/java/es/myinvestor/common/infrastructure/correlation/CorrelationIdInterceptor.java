package es.myinvestor.common.infrastructure.correlation;

import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * Interceptor to add correlation IDs to outgoing HTTP requests.
 * This ensures that correlation IDs are propagated to downstream services.
 */
@Component
public class CorrelationIdInterceptor implements ClientHttpRequestInterceptor {

    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution)
            throws IOException {
        // Add trace ID to outgoing request headers
        request.getHeaders().set(
                CorrelationIdConstants.REQUEST_TRACE_ID_HEADER,
                CorrelationIdHolder.getTraceId()
        );

        // Add username to outgoing request headers
        request.getHeaders().set(
                CorrelationIdConstants.REQUEST_USERNAME_HEADER,
                CorrelationIdHolder.getUsername()
        );

        // Continue with the execution chain
        return execution.execute(request, body);
    }
}