package es.myinvestor.common.infrastructure.rest;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.net.URI;
import java.time.Duration;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

public abstract class AbstractReactiveHttpClient {

  protected final WebClient client;
  protected final Duration timeout;
  protected final Retry retry;

  protected AbstractReactiveHttpClient(WebClient.Builder builder, String baseUrl, Duration timeout, Retry retry) {
    this.client = builder.baseUrl(baseUrl).build();
    this.timeout = timeout;
    this.retry = retry != null ? retry : Retry.max(0);
  }

  // -------- GET --------

  protected <T> Mono<T> get(String path, Class<T> responseType, Object... uriVars) {
    return client.get()
        .uri(path, uriVars)
        .accept(MediaType.APPLICATION_JSON)
        .retrieve()
        .onStatus(s -> s.is4xxClientError() || s.is5xxServerError(),
            resp -> resp.createException().flatMap(Mono::error))
        .bodyToMono(responseType)
        .timeout(timeout)
        .retryWhen(retry);
  }

  protected <T> Mono<T> get(Function<UriBuilderWrapper, URI> uriBuilderFn, Class<T> responseType) {
    return client.get()
        .uri(b -> uriBuilderFn.apply(new UriBuilderWrapper(b)))
        .accept(MediaType.APPLICATION_JSON)
        .retrieve()
        .onStatus(s -> s.is4xxClientError() || s.is5xxServerError(),
            resp -> resp.createException().flatMap(Mono::error))
        .bodyToMono(responseType)
        .timeout(timeout)
        .retryWhen(retry);
  }

  protected <T> Mono<T> get(String path, ParameterizedTypeReference<T> typeRef, Object... uriVars) {
    return client.get()
        .uri(path, uriVars)
        .accept(MediaType.APPLICATION_JSON)
        .retrieve()
        .onStatus(s -> s.is4xxClientError() || s.is5xxServerError(),
            resp -> resp.createException().flatMap(Mono::error))
        .bodyToMono(typeRef)
        .timeout(timeout)
        .retryWhen(retry);
  }

  protected <T> Mono<Optional<T>> getOptionalMono(String path, Class<T> responseType, Object... uriVars) {
    return client.get()
        .uri(path, uriVars)
        .accept(MediaType.APPLICATION_JSON)
        .retrieve()
        .onStatus(s -> s.value() == 404, resp -> Mono.empty()) // 404 -> empty
        .onStatus(s -> s.is4xxClientError() || s.is5xxServerError(),
            resp -> resp.createException().flatMap(Mono::error))
        .bodyToMono(responseType)
        .map(Optional::of)
        .timeout(timeout)
        .retryWhen(retry)
        .onErrorResume(ex -> Mono.just(Optional.empty()))
        .defaultIfEmpty(Optional.empty());
  }

  // -------- POST --------

  protected <B, R> Mono<R> post(String path, B body, Class<R> responseType, Map<String, String> headers) {
    WebClient.RequestBodySpec spec = client.post().uri(path)
        .contentType(MediaType.APPLICATION_JSON)
        .accept(MediaType.APPLICATION_JSON);

    if (headers != null) {
      headers.forEach(spec::header);
    }

    return spec.body(BodyInserters.fromValue(body))
        .retrieve()
        .onStatus(s -> s.is4xxClientError() || s.is5xxServerError(),
            resp -> resp.createException().flatMap(Mono::error))
        .bodyToMono(responseType)
        .timeout(timeout)
        .retryWhen(retry);
  }

  protected <B, R> Mono<R> post(String path, B body, ParameterizedTypeReference<R> typeRef) {
    return client.post()
        .uri(path)
        .contentType(MediaType.APPLICATION_JSON)
        .accept(MediaType.APPLICATION_JSON)
        .body(BodyInserters.fromValue(body))
        .retrieve()
        .onStatus(s -> s.is4xxClientError() || s.is5xxServerError(),
            resp -> resp.createException().flatMap(Mono::error))
        .bodyToMono(typeRef)
        .timeout(timeout)
        .retryWhen(retry);
  }

  protected <B, R> Mono<Optional<R>> postOptionalMono(String path, B body, Class<R> responseType) {
    return client.post()
        .uri(path)
        .contentType(MediaType.APPLICATION_JSON)
        .accept(MediaType.APPLICATION_JSON)
        .bodyValue(body)
        .retrieve()
        .onStatus(s -> s.value() == 404, resp -> Mono.empty())
        .onStatus(s -> s.is4xxClientError() || s.is5xxServerError(),
            resp -> resp.createException().flatMap(Mono::error))
        .bodyToMono(responseType)
        .map(Optional::of)
        .timeout(timeout)
        .retryWhen(retry)
        .onErrorResume(ex -> Mono.just(Optional.empty()))
        .defaultIfEmpty(Optional.empty());
  }

  // -------- Bridge a CF --------

  protected <T> CompletableFuture<T> toCompletableFuture(Mono<T> mono) {
    return mono.toFuture();
  }

  // -------- UriBuilder Wrapper --------
  protected static class UriBuilderWrapper {
    private final org.springframework.web.util.UriBuilder delegate;

    public UriBuilderWrapper(org.springframework.web.util.UriBuilder delegate) {
      this.delegate = delegate;
    }

    public UriBuilderWrapper path(String path) {
      delegate.path(path);
      return this;
    }

    public UriBuilderWrapper queryParam(String name, Object... values) {
      delegate.queryParam(name, values);
      return this;
    }

    public URI build(Object... uriVars) {
      return delegate.build(uriVars);
    }
  }
}
