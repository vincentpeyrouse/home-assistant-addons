#!/usr/bin/with-contenv bashio
# shellcheck shell=bash
set -e

#################
# NGINX SETTING #
#################


# Generate Ingress configuration
bashio::var.json \
    port "^$(bashio::addon.port 8482)" |
tempio \
    -template /etc/nginx/templates/ingress.gtpl \
    -out /etc/nginx/servers/ingress.conf