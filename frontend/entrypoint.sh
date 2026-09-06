#!/bin/sh
envsubst < /usr/share/nginx/html/public/config.template.json > /usr/share/nginx/html/public/config.json

exec nginx -c /etc/nginx/nginx.conf "$@"
