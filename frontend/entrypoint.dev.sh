#!/bin/sh
envsubst < public/config.template.json > public/config.json
exec "$@"
