package es.bitnomio.utilities.exceptions.api;

import es.bitnomio.utilities.exceptions.dtos.HttpRestExceptionBody;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Error;
import io.micronaut.http.hateoas.JsonError;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Controller
public class GlobalErrorController {

    private final Logger LOG = LoggerFactory.getLogger(GlobalErrorController.class);

    @Error(status = HttpStatus.NOT_FOUND, global = true)
    public HttpResponse<?> notFound(HttpRequest<?> request) {

        LOG.error("Resource not found global error");

        HttpRestExceptionBody error = new HttpRestExceptionBody(HttpStatus.NOT_FOUND.toString(), "Resource not found");
        return HttpResponse. <HttpRestExceptionBody>notFound().body(error);
    }

    @Error(status = HttpStatus.METHOD_NOT_ALLOWED, global = true)
    public HttpResponse<?> methodNotAllowed(HttpRequest<?> request) {

        LOG.error("Method not allowed global error");

        HttpRestExceptionBody error = new HttpRestExceptionBody(HttpStatus.METHOD_NOT_ALLOWED.toString(), "Method not allowed");
        return HttpResponse.<JsonError>notAllowed().body(error);
    }

    @Error(status = HttpStatus.UNSUPPORTED_MEDIA_TYPE, global = true)
    public HttpResponse<?> mediaTypeError(HttpRequest<?> request) {

        LOG.error("Media type not allowed global error");

        HttpRestExceptionBody error = new HttpRestExceptionBody(HttpStatus.UNSUPPORTED_MEDIA_TYPE.toString(), "Media type not allowed");
        return HttpResponse.status(HttpStatus.UNSUPPORTED_MEDIA_TYPE).body(error);
    }

    @Error(status = HttpStatus.BAD_REQUEST, global = true)
    public HttpResponse<?> badRequestError(HttpRequest<?> request) {

        LOG.error("Request error or payload could not be validated global error");

        HttpRestExceptionBody error = new HttpRestExceptionBody(HttpStatus.BAD_REQUEST.toString(), "Request error or payload could not be validated");
        return HttpResponse.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @Error(status = HttpStatus.UNAUTHORIZED, global = true)
    public HttpResponse<?> unauthorizedRequestError(HttpRequest<?> request) {

        LOG.error("Wrong credentials, token expired or not present global error");

        HttpRestExceptionBody error = new HttpRestExceptionBody(HttpStatus.UNAUTHORIZED.toString(), "Wrong credentials, token expired or not present");
        return HttpResponse.status(HttpStatus.UNAUTHORIZED).body(error);
    }

}

