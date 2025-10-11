package es.bitnomio.utilities.tracing.correlationId.http;

import es.bitnomio.utilities.constants.Request;
import es.bitnomio.utilities.utils.MDCUtils;
import io.micronaut.context.annotation.Value;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.MutableHttpRequest;
import io.micronaut.http.annotation.Filter;
import io.micronaut.http.filter.ClientFilterChain;
import io.micronaut.http.filter.HttpClientFilter;
import org.reactivestreams.Publisher;
import org.slf4j.Logger;

@Filter("/**")
public class RequestIdClientFilter implements HttpClientFilter {

    private static final Logger log = org.slf4j.LoggerFactory.getLogger(RequestIdClientFilter.class);
    @Value("${app.tracing.enabled:false}")
    Boolean traceEnabled;

    /**
     *
     * This Http client filter enhances requests with our BitnomioOptId and BitnomioUsername.
     * By now this filter applies to all output request but this is not ideal, our providers
     * doesn't need this information.
     *
     * @param request output request
     * @param chain   filter chain
     * @return a response from the other side ;)
     */
    @Override public Publisher<? extends HttpResponse<?>> doFilter(MutableHttpRequest<?> request, ClientFilterChain chain) {

        if (traceEnabled) {

            try {
                request.getHeaders().add(Request.Header.REQUEST_ID, MDCUtils.getRequestId());
            } catch (Exception e) {
                log.error("Error adding operation id header to outgoing request. Look after async events with no MDC sync.");
            }

            try {
                request.getHeaders().add(Request.Header.REQUEST_USER, MDCUtils.getUser());
            } catch (Exception e) {
                log.error("Error adding username header to outgoing request. Look after async events with no MDC sync.");
            }

        }

        return chain.proceed(request);
    }
}
