package es.bitnomio.utilities.utils;

import es.bitnomio.utilities.constants.AppConfig;
import es.bitnomio.utilities.constants.Request;
import io.micronaut.http.HttpRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.inject.Singleton;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static es.bitnomio.utilities.constants.AppConfig.Endpoints.LOGIN_MARKER;

/**
 * TODO Cache request values with requestId
 * Param requestId is not needed, nor used but to caching purposes in any moment
 * <p>
 * This class provides the utils needed to work with request headers, easing things
 * for dev team.
 * <p>
 * 2024 [Peter]
 */
@Singleton
public final class RequestUtils {

    private static final Logger log = LoggerFactory.getLogger(RequestUtils.class.getName());

    /**
     * Extracts the username from jwt token if present or req body on login
     *
     * @param request HttpRequest
     * @return the username
     */
    public static String getUserNameFromRequest(final HttpRequest<?> request) {

        if (log.isDebugEnabled()) {
            log.debug("Trying to get username from request.");
        }

        if (!request.getUri().getPath().endsWith(LOGIN_MARKER)) {

            Optional<String> bearer = getBearerFromRequestHeaders(request);

            if (bearer.isPresent()) {

                if (log.isDebugEnabled()) {
                    log.debug("Bearer is present in request");
                }

                String token = bearer.get();

                try {

                    return getServiceUserForTraces(JWTUtils.getClaims(token));

                } catch (Exception e) {
                    log.error("Mandatory claim subject not found in token");
                }
            }
        }

        return AppConfig.Markers.BITNOMIO;
    }

    private static String getServiceUserForTraces(final Map<String, Object> bodyMap) {

        if (bodyMap.containsKey("phonePrefix") && bodyMap.containsKey("phoneNumber")) {
            return bodyMap.get("phonePrefix") + AppConfig.Markers.SPLIT + bodyMap.get("phoneNumber");
        } else if (bodyMap.containsKey("code")) {
            return bodyMap.get("code").toString();
        } else if (bodyMap.containsKey("sub")) {
            return bodyMap.get("sub").toString();
        }

        return AppConfig.Markers.BITNOMIO;
    }

    /**
     * @param request the request
     * @return an Optional of the Authorization header
     */
    public static Optional<String> getBearerFromRequestHeaders(final HttpRequest<?> request) {
        return Optional.ofNullable(request.getHeaders().get("Authorization"));
    }

    /**
     * @param request   the http request
     * @param headerKey the header key
     * @return the header value
     */
    public Optional<String> getHeaderValueFromRequest(final HttpRequest<?> request, final String headerKey) {
        String headerValue = null;
        Optional<List<String>> optHeaderValue = getHeaderRawValue(request, headerKey);

        try {
            if (optHeaderValue.isPresent()) {
                headerValue = optHeaderValue.get().get(0);
            }
        } catch (Exception e) {
            log.error("Error extracting value from request header: {} ", headerKey);
        }

        return Optional.ofNullable(headerValue);
    }

    /**
     * @param request   the http request
     * @param headerKey the header key
     * @return the header RAW value, a list of String
     */
    public Optional<List<String>> getHeaderRawValue(final HttpRequest<?> request, final String headerKey) {

        return Optional.ofNullable(request.getHeaders().asMap().get(headerKey));

    }

    /**
     * @param request the http request
     * @return a map of Strings with all headers
     */
    public Map<String, List<String>> getHeaders(final HttpRequest<?> request) {

        return request.getHeaders().asMap();

    }

    /**
     * @param request the http request
     * @return raw body as string
     */
    public Optional<String> getRawStringBody(final HttpRequest<?> request) {

        return request.getBody(Request.Body.BODY_AS_STRING);

    }

    /**
     * @param request the http request
     * @return body as key value map
     */
    public Map<String, Object> getMappedBody(final HttpRequest<?> request) {
        String rawBody = getRawStringBody(request).orElse("");
        return JsonUtils.jsonToMap(rawBody);

    }

}
