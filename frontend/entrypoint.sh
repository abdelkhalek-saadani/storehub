#!/bin/sh
envsubst < /usr/share/nginx/html/config.template.json > /usr/share/nginx/html/config.json

exec nginx -c /etc/nginx/nginx.conf "$@"
