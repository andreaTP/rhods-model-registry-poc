# Apicurio Registry With External Ref Example

This is an extension to Apicurio Registry that will implement external references as an "Artifact Type".
The initial implementation will support External references, possibly as S3 buckets.

# Build

```
mvn clean install
```

# Run

```
cd target/
java -jar apicurio-registry-external-ref-0.0.1-SNAPSHOT-runner.jar
```
