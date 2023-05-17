package io.apicurio.registry.rules;


import com.fasterxml.jackson.databind.ObjectMapper;
import io.apicurio.registry.types.extref.ExternalReference;
import lombok.SneakyThrows;

public class ExternalReferenceBuilder {

    private ExternalReference instance;
    private final ObjectMapper mapper = new ObjectMapper();

    public ExternalReferenceBuilder() {}

    @SneakyThrows
    public ExternalReferenceBuilder fromContent(String content) {
        instance = mapper.readValue(content, ExternalReference.class);
        return this;
    }

    public ExternalReference build() {
        return instance;
    }
}
