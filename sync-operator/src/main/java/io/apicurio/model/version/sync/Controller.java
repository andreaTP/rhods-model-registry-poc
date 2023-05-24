package io.apicurio.model.version.sync;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.apicurio.registry.rest.client.RegistryClientFactory;
import io.apicurio.v1.ModelVersion;
import io.apicurio.v1.ModelVersionSpec;
import io.apicurio.v1.ModelVersionStatus;
import io.fabric8.kubernetes.api.model.OwnerReferenceBuilder;
import io.javaoperatorsdk.operator.api.reconciler.Context;
import io.javaoperatorsdk.operator.api.reconciler.ControllerConfiguration;
import io.javaoperatorsdk.operator.api.reconciler.ErrorStatusHandler;
import io.javaoperatorsdk.operator.api.reconciler.ErrorStatusUpdateControl;
import io.javaoperatorsdk.operator.api.reconciler.EventSourceContext;
import io.javaoperatorsdk.operator.api.reconciler.EventSourceInitializer;
import io.javaoperatorsdk.operator.api.reconciler.Reconciler;
import io.javaoperatorsdk.operator.api.reconciler.UpdateControl;
import io.javaoperatorsdk.operator.processing.event.source.EventSource;
import io.javaoperatorsdk.operator.processing.event.source.polling.PerResourcePollingEventSource;
import io.kserve.serving.v1beta1.InferenceService;
import io.kserve.serving.v1beta1.InferenceServiceBuilder;
import io.quarkus.logging.Log;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Set;

@ControllerConfiguration()
public class Controller implements Reconciler<ModelVersion>, ErrorStatusHandler<ModelVersion>, EventSourceInitializer<ModelVersion> {

    ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public UpdateControl<ModelVersion> reconcile(ModelVersion modelVersion, Context<ModelVersion> context) throws Exception {
        Log.infof("Reconcile loop starting for Model Version %s", modelVersion.getMetadata().getName());
        var namespace = modelVersion.getMetadata().getNamespace();
        var prevInferenceService = context
                .getClient()
                .resources(InferenceService.class)
                .inNamespace(namespace)
                .withName(modelVersion.getMetadata().getName())
                .get();

        String currentPath = null;
        try {
            currentPath = prevInferenceService
                    .getSpec()
                    .getPredictor()
                    .getModel()
                    .getStorage()
                    .getPath();
        } catch (Exception e) {
            // ignore
        }

        String path = null;
        if (modelVersion.getSpec().getMode().equals(ModelVersionSpec.Mode.FIXED)) {
            // If version is fixed we simply use it as the object name
            Log.infof("Using fixed version: %s", modelVersion.getSpec().getArtifactVersion());
            path = modelVersion.getSpec().getArtifactVersion();
        } else if (context.getSecondaryResource(String.class).isPresent()) {
            // If version has to be latest we should poll
            Log.infof("Using LATEST: %s", path);
            path = context.getSecondaryResource(String.class).get();
        } else {
            // throw exception?
            throw new RuntimeException("OOOPS");
        }

        Log.infof("Current artifact version: %s, current path: %s, final path is: %s", modelVersion.getMetadata().getName(), currentPath, path);
        if (path != null && path.equals(currentPath)) {
            return UpdateControl.noUpdate();
        } else {
            context
                    .getClient()
                    .resources(InferenceService.class)
                    .createOrReplace(getInferenceService(modelVersion, path));

            ModelVersionStatus status = new ModelVersionStatus();
            status.setStatus("Synced");
            status.setArtifactVersion(path);
            modelVersion.setStatus(status);
            return UpdateControl.updateStatus(modelVersion);
        }
    }

    private InferenceService getInferenceService(ModelVersion modelVersion, String path) {
        return new InferenceServiceBuilder()
                .withNewMetadata()
                    .withName(modelVersion.getMetadata().getName())
                    .withNamespace(modelVersion.getMetadata().getNamespace())
                    .withAnnotations(Map.of("serving.kserve.io/deploymentMode", "ModelMesh"))
                    .withOwnerReferences(
                            new OwnerReferenceBuilder()
                                    .withController(true)
                                    .withBlockOwnerDeletion(true)
                                    .withApiVersion(modelVersion.getApiVersion())
                                    .withKind(modelVersion.getKind())
                                    .withName(modelVersion.getMetadata().getName())
                                    .withUid(modelVersion.getMetadata().getUid())
                                    .build())
                .endMetadata()
                .withNewSpec()
                    .withNewPredictor()
                        .withNewModel()
                            .withNewModelFormat()
                                .withName("sklearn")
                            .endModelFormat()
                            .withRuntime("mlserver-0.x")
                            .withNewModelStorage()
                                .withKey("registryMinio")
                                .withPath(path)
                            .endModelStorage()
                        .endModel()
                    .endPredictor()
                .endSpec()
                .build();
    }

    @Override
    public ErrorStatusUpdateControl<ModelVersion> updateErrorStatus(ModelVersion modelVersion, Context<ModelVersion> context, Exception e) {
        ModelVersionStatus status = new ModelVersionStatus();
        status.setStatus("ERROR: " + e.getMessage());
        status.setArtifactVersion(null);
        modelVersion.setStatus(status);
        return ErrorStatusUpdateControl.updateStatus(modelVersion);
    }

    @Override
    public Map<String, EventSource> prepareEventSources(EventSourceContext<ModelVersion> eventSourceContext) {
        return EventSourceInitializer.nameEventSources(new PerResourcePollingEventSource<String, ModelVersion>(
                modelVersion -> {
                    try {
                        var client = RegistryClientFactory.create(modelVersion.getSpec().getRegistryUrl());
                        InputStream is = client.getLatestArtifact("default", modelVersion.getSpec().getArtifactName());
                        var value = new String(is.readAllBytes(), StandardCharsets.UTF_8);
                        var json = objectMapper.readTree(value);
                        var path = json.get("object_name").asText();
//                        Log.infof("Artifact: %s is available at the id %s", modelVersion.getSpec().getArtifactName(), path);
                        return Set.of(path);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                        // or return empty?
                    }
                }, eventSourceContext.getPrimaryCache(), 1000, String.class));
    }
}
