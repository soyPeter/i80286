ARG JAEGER_VERSION
ARG JAEGER_TARGET_ARCH

FROM jaegertracing/all-in-one:${JAEGER_VERSION} AS release

LABEL maintainer="Peter <pa@bitnomio.es>"

# Agent zipkin.thrift compact
EXPOSE 5775/udp

# Agent jaeger.thrift compact
EXPOSE 6831/udp

# Agent jaeger.thrift binary
EXPOSE 6832/udp

# Agent config HTTP
EXPOSE 5778

# Collector HTTP
EXPOSE 14268

# Collector gRPC
EXPOSE 14250

# Web HTTP
EXPOSE 16686

VOLUME ["/tmp"]

