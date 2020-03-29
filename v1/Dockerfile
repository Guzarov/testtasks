FROM openjdk:11
COPY build/libs/*.jar /app.jar

ENTRYPOINT java \
    -Dspring.profiles.active=${GENERATOR_IMPL:-default} \
    -Dgenerator.diapasons.size=${DIAPASON_SIZE:-100} \
    -Dmemcached.host=${MEMCACHED_HOST:-localhost} \
    -Dmemcached.port=${MEMCACHED_PORT:-11211} \
    -jar /app.jar