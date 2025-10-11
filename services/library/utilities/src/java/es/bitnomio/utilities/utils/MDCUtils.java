package es.bitnomio.utilities.utils;

import es.bitnomio.utilities.constants.AppConfig;
import org.slf4j.MDC;

import java.util.UUID;


/**
 * <p>
 * This class provides logging MDC Bitnomio customized utils.
 * MDC provides us a way to trace properly any request without
 * any extra effort. The username and operation id is binded for
 * every request and its attached to every logback trace.
 * </p>
 * <p>
 * This is a very simple implementation, and will work perfectly in
 * a thread per request based application, on an async application
 * we should do a little wiring in order for this to work.
 * </p>
 * <p>
 * @see <a href="https://logging.apache.org/log4j/1.2/apidocs/org/apache/log4j/MDC.html">Mapped Diagnostic Contex</a>
 * </p>
 *
 * <p>Bitnomio 2024</p>
 */
 // TODO: Research and invest some time for MDC async context synchronization for multithreaded apps.
public final class MDCUtils {

    /**
     * @return user in context labeled with  {@link AppConfig.MDC.REQUEST_USER}
     */
    public static String getUser() {

        var user = MDC.get(AppConfig.MDC.REQUEST_USER);

        return user != null ? user : "BITNOMIO";
    }

    /**
     * @return operation id in context labeled with  {@link AppConfig.MDC.REQUEST_ID}
     */
    public static String getRequestId() {
        return MDC.get(AppConfig.MDC.REQUEST_ID);
    }

    /**
     * Puts default app {@link AppConfig.MDC.REQUEST_USER}
     * and a random operation id in Context
     */
    public static void setDefaultUsernameWithRandomOptId() {
        MDC.put(AppConfig.MDC.REQUEST_ID, UUID.randomUUID().toString());
        MDC.put(AppConfig.MDC.REQUEST_USER, AppConfig.MDC.DEFAULT_USER);
    }

    /**
     * Puts in Context Bitnomio request id and username
     *
     * @param requestId    Bitnomio operation id
     * @param username Bitnomio username
     */
    public static void setOptIdAndUsername(String requestId, String username) {
        MDC.put(AppConfig.MDC.REQUEST_ID, requestId);
        MDC.put(AppConfig.MDC.REQUEST_USER, username);
    }

    /**
     * Removes from Context request id and username
     */
    public static void removeUsernameAndOptId() {
        MDC.remove(AppConfig.MDC.REQUEST_ID);
        MDC.remove(AppConfig.MDC.REQUEST_USER);
    }

    /**
     * Establish the service name to be accessible from the Context
     *
     * @param name service name from micronaut properties
     */
    public static void setServiceName(String name) {
        MDC.put(AppConfig.MDC.SERVICE_NAME, name);
    }

    /**
     * @return application service name from Context
     */
    public static String getServiceName() {
        return MDC.get(AppConfig.MDC.SERVICE_NAME);
    }


}
