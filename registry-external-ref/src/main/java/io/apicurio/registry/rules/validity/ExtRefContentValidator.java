package io.apicurio.registry.rules.validity;

import io.apicurio.registry.content.ContentHandle;
import io.apicurio.registry.rules.ExternalReferenceBuilder;
import io.apicurio.registry.rules.RuleViolationException;
import io.apicurio.registry.types.RuleType;

import java.util.Map;

public class ExtRefContentValidator extends ExternalReferenceBuilder implements ContentValidator {

    @Override
    public void validate(ValidityLevel level, ContentHandle artifactContent, Map<String, ContentHandle> resolvedReferences)
            throws RuleViolationException {
        try {
            ExternalReferenceBuilder builder = new ExternalReferenceBuilder();
            builder.fromContent(artifactContent.content()).build();
        } catch (Exception e) {
            throw new RuleViolationException("invalid external reference schema", RuleType.VALIDITY, null, e);
        }
    }

}
