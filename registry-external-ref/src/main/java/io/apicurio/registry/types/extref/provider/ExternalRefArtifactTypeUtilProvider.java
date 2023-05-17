package io.apicurio.registry.types.extref.provider;

import io.apicurio.registry.content.canon.ContentCanonicalizer;
import io.apicurio.registry.content.canon.JsonContentCanonicalizer;
import io.apicurio.registry.content.dereference.ContentDereferencer;
import io.apicurio.registry.content.extract.ContentExtractor;
import io.apicurio.registry.content.extract.JsonContentExtractor;
import io.apicurio.registry.content.extract.NoopContentExtractor;
import io.apicurio.registry.rules.compatibility.ExtRefCompatibilityChecker;
import io.apicurio.registry.rules.compatibility.CompatibilityChecker;
import io.apicurio.registry.rules.validity.ExtRefContentValidator;
import io.apicurio.registry.rules.validity.ContentValidator;
import io.apicurio.registry.types.provider.AbstractArtifactTypeUtilProvider;

public class ExternalRefArtifactTypeUtilProvider extends AbstractArtifactTypeUtilProvider {
    @Override
    protected CompatibilityChecker createCompatibilityChecker() {
        return new ExtRefCompatibilityChecker();
    }

    @Override
    protected ContentCanonicalizer createContentCanonicalizer() {
        return new JsonContentCanonicalizer();
    }

    @Override
    protected ContentValidator createContentValidator() {
        return new ExtRefContentValidator();
    }

    @Override
    protected ContentExtractor createContentExtractor() { return NoopContentExtractor.INSTANCE; }

    @Override
    public String getArtifactType() { return "EXTREF"; }

    @Override
    public ContentDereferencer getContentDereferencer() {
        return null;
    }
}
