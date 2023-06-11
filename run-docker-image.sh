#!/bin/bash

docker run -d \
--name demo-tenant \
-p 9000:9000 \
--env-file .env \
demo-tenant:latest