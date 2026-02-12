#!/bin/sh

# Fix Render's DATABASE_URL for Spring Boot / JDBC
# Render gives: postgres://app:password@host/db
# JDBC needs: jdbc:postgresql://host/db?user=app&password=password
# OR simply: jdbc:postgresql://app:password@host/db (The driver supports this!)
# So we just need to replace 'postgres://' with 'jdbc:postgresql://'

if [ -n "$DATABASE_URL" ]; then
    # Strip the protocol
    CLEAN_URL=$(echo "$DATABASE_URL" | sed -E 's|^postgres(ql)?://||')

    # Extract User and Password (everything before the last @)
    USER_PASS=$(echo "$CLEAN_URL" | sed -E 's|@([^@]*)$||')
    
    # Extract Host and DB (everything after the last @)
    HOST_DB=$(echo "$CLEAN_URL" | sed -E 's|^.*@||')

    # Split User and Password
    DB_USER=$(echo "$USER_PASS" | cut -d: -f1)
    DB_PASS=$(echo "$USER_PASS" | cut -d: -f2-)

    # Export Spring Boot environment variables
    export SPRING_DATASOURCE_USERNAME="$DB_USER"
    export SPRING_DATASOURCE_PASSWORD="$DB_PASS"
    export SPRING_DATASOURCE_URL="jdbc:postgresql://$HOST_DB"

    echo "Configured JDBC URL: $SPRING_DATASOURCE_URL"
fi

# Run the app
exec java -jar -Dspring.profiles.active=render app.jar
