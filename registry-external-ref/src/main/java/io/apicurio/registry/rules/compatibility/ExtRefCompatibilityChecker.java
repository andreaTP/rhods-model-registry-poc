package io.apicurio.registry.rules.compatibility;

import io.apicurio.registry.content.ContentHandle;
import io.apicurio.registry.rules.ExternalReferenceBuilder;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ExtRefCompatibilityChecker extends ExternalReferenceBuilder implements CompatibilityChecker {
    @Override
    public CompatibilityExecutionResult testCompatibility(CompatibilityLevel compatibilityLevel, List<ContentHandle> existingArtifacts, ContentHandle proposedArtifact, Map<String, ContentHandle> resolvedReferences) {
        // existingArtifacts is implemented as a LazyContentList, which does not support the spliterator interface.
        // This means we cannot call stream() on it in java version 11 (does work in version 17)
        final List<String> existingArtifactsContent = new ArrayList<>();
        existingArtifacts.forEach(ch -> existingArtifactsContent.add(ch.content()));
        return testCompatibility(compatibilityLevel, existingArtifactsContent,
                proposedArtifact.content(), resolvedReferences);
    }

    @Override
    public CompatibilityExecutionResult testCompatibility(CompatibilityLevel compatibilityLevel, List<String> existingArtifacts, String proposedArtifact, Map<String, ContentHandle> resolvedReferences) {
        if (compatibilityLevel.equals(CompatibilityLevel.NONE)) {
            return CompatibilityExecutionResult.compatible();
        } else {
            throw new RuntimeException("Compatibility level is not supported");
        }
    }
}
