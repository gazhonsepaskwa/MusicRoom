#!/bin/bash

set -e

export DATABASE_URL="$(cat /run/secrets/database_url)"

npx prisma generate

echo "DB is up, running migrations..."

# npx prisma migrate reset --force

npx prisma migrate deploy

npm run start:dev

