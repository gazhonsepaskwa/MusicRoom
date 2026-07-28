#!/bin/bash

migration_name="$1"

export DATABASE_URL="$(cat /run/secrets/database_url)"

if [ -z "$migration_name" ]; then
  echo "No migration name provided."
  exit 0
fi

npx prisma migrate reset

npx prisma migrate dev --name $migration_name