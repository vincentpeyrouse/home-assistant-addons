FROM ghcr.io/hassio-addons/base-nodejs:0.2.5

ENV SUPERVISOR_TOKEN ""
VOLUME /config

RUN apk -U update && \
    apk -U upgrade && \
    apk -U add --no-cache \
    nginx

COPY rootfs/ /

RUN chmod +x /etc/cont-init.d/30-nginx.sh
RUN chmod +x /docker-entrypoint.sh

ARG package_version
LABEL \
  io.hass.version="$package_version" \
  io.hass.type="addon" \
  io.hass.arch="armhf|aarch64|i386|amd64"

RUN npm install -g home-assistant-matter-hub

CMD /docker-entrypoint.sh
