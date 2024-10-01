# Dockerfile-postgres
FROM amazoncorretto:18.0.2-alpine3.16
WORKDIR /app
ARG JAR_FILE=*.jar
COPY pepegaVpnManager-1.0-SNAPSHOT.jar /app/pepegaVpnManager.jar
RUN apk upgrade --update \
    && apk add bash screen  && \
    set -ex && \
    echo 'http://dl-cdn.alpinelinux.org/alpine/edge/testing' >> /etc/apk/repositories && \
    echo 'http://dl-cdn.alpinelinux.org/alpine/edge/main' >> /etc/apk/repositories && \
    apk --update add --no-cache \
    curl \
    privoxy \
    libsodium \
    mbedtls \
    udns \
    pcre \
    iproute2 \
    libev \
    shadowsocks-libev && \
    mkdir -p /etc/shadowsocks-libev
EXPOSE 8800
ENTRYPOINT ["java", "-jar", "pepegaVpnManager.jar"]