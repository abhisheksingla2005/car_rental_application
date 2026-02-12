#!/bin/sh

# Fix Render's DATABASE_URL for Spring Boot / JDBC
# Render gives: postgres://app:password@host/db
# JDBC needs: jdbc:postgresql://host/db?user=app&password=password
# OR simply: jdbc:postgresql://app:password@host/db (The driver supports this!)
# So we just need to replace 'postgres://' with 'jdbc:postgresql://'

if [ -n "$DATABASE_URL" ]; then
    export SPRING_DATASOURCE_URL=$(echo $DATABASE_URL | sed 's|^postgres://|jdbc:postgresql://|')
    echo "Configured SPRING_DATASOURCE_URL from DATABASE_URL"
fi

# Run the app
exec java -jar -Dspring.profiles.active=render app.jar
