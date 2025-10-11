package es.bitnomio.utilities.utils;

import io.micronaut.http.HttpHeaderValues;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import es.bitnomio.utilities.exceptions.UnauthorizedException;


import java.util.Map;
import java.util.Optional;

public final class JWTUtils {

    private static final Logger log = LoggerFactory.getLogger(JWTUtils.class.getName());

    public static final String BEARER_MARKER = HttpHeaderValues.AUTHORIZATION_PREFIX_BEARER;
    private static final int TOKEN_HEADER = 0;
    private static final int TOKEN_BODY = 1;
    private static final int TOKEN_SIGNATURE = 2;

    /**
     * @param token Bearer token
     * @param claim Claim to retrieve from token
     * @return Nullable Optional of the claim
     */
    public static Optional<String> getClaim(@NotNull @NotBlank String token,
                                            @NotNull @NotBlank String claim) {

        Map<String, Object> claims = getClaims(token);

        if (claims.isEmpty()) {
            return Optional.empty();
        }

        return Optional.ofNullable(claims.get(claim).toString());
    }

    /**
     * @param token Bearer token
     * @return all claims in token
     */
    public static Map<String, Object> getClaims(@NotNull @NotBlank String token) {

        String cleanToken = cleanBearerMarkerFromToken(token);
        String claims = extractTokenBody(cleanToken);

        return JsonUtils.jsonToMap(claims);
    }

    /**
     * Cleans token from header
     *
     * @param token JWT token valid (ensured by Micronaut Security package)
     * @return Encoded Token String without Bearer marker
     */
    public static String cleanBearerMarkerFromToken(String token) {
        if (StringUtils.isBlank(token)) {
            return token;
        }
        if (!token.contains(BEARER_MARKER)) {
            return token;
        }
        return token.replace(BEARER_MARKER, "").trim();
    }

    private static String extractTokenHeader(String token) {
        return extractFromToken(token, TOKEN_HEADER);
    }

    private static String extractTokenBody(String token) {
        return extractFromToken(token, TOKEN_BODY);
    }

    private static String extractTokenSignature(String token) {
        return extractFromToken(token, TOKEN_SIGNATURE);
    }

    private static String extractFromToken(String token, int tokenPart) {

        if (token.isEmpty()) {
            log.debug("Empty token. Cant extract token body.");
            return "";
        }

        try {
            String[] tokenSplit = splitToken(token);
            Base64 base64Url = new Base64(true);
            return new String(base64Url.decode(tokenSplit[tokenPart]));
        }
        catch (Exception e) {
            log.warn("Token NOT VALID: {}", token);
            throw new UnauthorizedException();
        }
    }

    private static String[] splitToken(String token) {
        return token.split("\\.");
    }


}
