FROM registry.access.redhat.com/ubi8/openjdk-17:latest

# limiting Xmx to 200 as the fly machine has only 256 Mb of RAM in current configuration
ENV JAVA_APP_DIR=/deployments \
    JAVA_APP_JAR=apicurio-registry-external-ref-0.0.1-SNAPSHOT-runner.jar \
    JAVA_OPTIONS="-Dquarkus.http.host=0.0.0.0 -Djava.util.logging.manager=org.jboss.logmanager.LogManager -Xmx400m"

EXPOSE 8080 8778 9779

USER 185

COPY target/lib /deployments/lib
COPY target/apicurio-registry-*.jar /deployments/
