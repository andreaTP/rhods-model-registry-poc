package io.apicurio.model.version.sync;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.microsoft.kiota.authentication.AnonymousAuthenticationProvider;
import com.microsoft.kiota.http.OkHttpRequestAdapter;
import io.apicurio.v1.ModelVersion;
import io.apicurio.v1.ModelVersionSpec;
import io.apicurio.v1.ModelVersionStatus;
import io.javaoperatorsdk.operator.api.reconciler.Context;
import io.javaoperatorsdk.operator.api.reconciler.ErrorStatusHandler;
import io.javaoperatorsdk.operator.api.reconciler.ErrorStatusUpdateControl;
import io.javaoperatorsdk.operator.api.reconciler.Reconciler;
import io.javaoperatorsdk.operator.api.reconciler.UpdateControl;
import io.kserve.serving.v1beta1.InferenceService;
import io.kserve.serving.v1beta1.InferenceServiceBuilder;

import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

public class Controller implements Reconciler<ModelVersion>, ErrorStatusHandler<ModelVersion> {

    ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public UpdateControl<ModelVersion> reconcile(ModelVersion modelVersion, Context<ModelVersion> context) throws Exception {
        var namespace = modelVersion.getMetadata().getNamespace();
        var prevInferenceService = context
                .getClient()
                .resources(InferenceService.class)
                .inNamespace(namespace)
                .withName(modelVersion.getMetadata().getName())
                .get();

        String currentArtifactVersion = null;
        try {
            currentArtifactVersion = modelVersion.getStatus().getArtifactVersion();
        } catch (Exception e) {
            // ignore
        }

        String currentPath = null;
        try {
            prevInferenceService
                    .getSpec()
                    .getPredictor()
                    .getModel()
                    .getStorage()
                    .getPath();
        } catch (Exception e) {
            // ignore
        }

        AtomicReference<String> path = new AtomicReference<>();
        if (modelVersion.getSpec().getMode().equals(ModelVersionSpec.Mode.FIXED)) {
            // If version is fixed we simply use it as the object name
            path.setPlain(currentArtifactVersion);
        } else {
            // If version has to be latest we should poll
            // This section needs to be a PollResource
            var adapter = new OkHttpRequestAdapter(new AnonymousAuthenticationProvider());
            var client = new io.apicurio.registry.client.ApiClient(adapter);

            client
                    .groups("default")
                    .artifacts(modelVersion.getSpec().getArtifactName())
                    .get()
                    .thenAccept(is -> {
                        try {
                            var json = objectMapper.readTree(is);
                            path.setPlain(json.get("object_name").asText());
                        } catch (IOException e) {
                            // ignore
                        }
                    })
                    // TODO: FIXME using a fully async. I agree, this is horrific!
                    .get(10, TimeUnit.SECONDS);
        }


        if (currentArtifactVersion != null && currentArtifactVersion.equals(currentPath)) {
            return UpdateControl.noUpdate();
        } else {
            context
                    .getClient()
                    .resources(InferenceService.class)
                    .createOrReplace(getInferenceService(modelVersion, path.get()));

            modelVersion.getStatus().setStatus("Synced");
            modelVersion.getStatus().setArtifactVersion(path.get());
            return UpdateControl.updateStatus(modelVersion);
        }
    }

    private InferenceService getInferenceService(ModelVersion modelVersion, String path) {
        return new InferenceServiceBuilder()
                .withNewMetadata()
                    .withName(modelVersion.getMetadata().getName())
                    .withNamespace(modelVersion.getMetadata().getNamespace())
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
}
