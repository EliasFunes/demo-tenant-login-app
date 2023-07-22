#!/bin/bash

sh build-docker-image.sh

docker tag demo-tenant:latest eliasfunes/qrlogindemo:latest

docker push eliasfunes/qrlogindemo:latest

echo "Docker push successfully."