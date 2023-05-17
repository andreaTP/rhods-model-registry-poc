package io.apicurio.registry.types.extref;

// Represents an external reference object
// identified by a url and a reference
public final class ExternalReference {

    private final String url;
    private final String reference;

    public ExternalReference(String url, String reference) {
        this.url = url;
        this.reference = reference;
    }

    public String getUrl() {
        return url;
    }

    public String getReference() {
        return reference;
    }
}
