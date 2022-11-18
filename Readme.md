# Apicurio Registry With BigQuery Support Example

Motivated by this PR:
https://github.com/Apicurio/apicurio-registry/pull/2923

we decided to make Registry "extensible" by the users regarding artifact type support.

This project shows how it's possible to extend Apicurio Registry and bundle up a working service including a custom Artifact Type support.

# Build

```
mvn clean install
```

# Run

```
cd target/
java -jar apicurio-registry-with-bigquery-0.0.1-SNAPSHOT-runner.jar
```

### Known limitations

 - when creating an artifact of a type not included in the default you need to ALWAYS specify the appropriate ArtifactType
 - the UI will show the plain name of the additional type and won't have an appropriate icon to identify it
