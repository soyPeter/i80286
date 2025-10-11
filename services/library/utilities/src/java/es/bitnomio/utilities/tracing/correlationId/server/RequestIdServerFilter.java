package es.bitnomio.utilities.tracing.correlationId.server;

import es.bitnomio.utilities.constants.Request;
import es.bitnomio.utilities.utils.MDCUtils;
import es.bitnomio.utilities.utils.RequestUtils;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.MutableHttpResponse;
import io.micronaut.http.annotation.Filter;
import io.micronaut.http.filter.HttpServerFilter;
import io.micronaut.http.filter.ServerFilterChain;
import org.reactivestreams.Publisher;
import org.slf4j.Logger;
import org.slf4j.MDC;
import reactor.core.publisher.Flux;

import java.util.Collections;
import java.util.UUID;


@Filter("/**")
public class RequestIdServerFilter implements HttpServerFilter {

    private static final Logger
        log
        = org.slf4j.LoggerFactory.getLogger(RequestIdServerFilter.class);

    /**
     * @param request incoming http request
     * @param chain   non-blocking and thread-safe filter chain.
     * @return the chain
     */
    @Override public Publisher<MutableHttpResponse<?>> doFilter(HttpRequest<?> request, ServerFilterChain chain) {
        // try to find any of the valid trace request headers
        final String requestId = request
            .getHeaders()
            .findFirst(Request.Header.REQUEST_ID)
            .orElse(request
                .getHeaders()
                .findFirst(Request.Header.REQUEST_ID)
                .orElse(UUID.randomUUID().toString()));

        final String requestUser = request.getHeaders()
            .getFirst(Request.Header.REQUEST_USER)
            .orElse(RequestUtils.getUserNameFromRequest(request));

        MDCUtils.setOptIdAndUsername(requestId, requestUser);

        MDC.put(Request.Header.REQUEST_ID, requestId);

        return Flux.from(chain.proceed(request))
            .doOnNext(req -> {
                try {
                    req.getHeaders().add(Request.Header.REQUEST_ID, requestId);
                }
                catch (Exception e) {
                    log.error(e.getMessage());
                }
            })
            .doOnError(error -> {
                if (error instanceof NullPointerException) {
                    log.error("NPE - This should never ever happen but it could :D");
                }
            }).doFinally(p -> {
                request.getHeaders()
                    .asMap()
                    .put(Request.Header.REQUEST_ID, Collections.singletonList(requestId));

                MDC.remove(Request.Header.REQUEST_ID);
                MDCUtils.removeUsernameAndOptId();
            });
    }



    @Override
    public int getOrder() {
        return 0;
    }
}
