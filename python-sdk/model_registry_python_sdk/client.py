import json
import os

from kiota_abstractions.authentication.anonymous_authentication_provider import (
    AnonymousAuthenticationProvider,
)
from kiota_http.httpx_request_adapter import HttpxRequestAdapter
from minio import Minio
from apicurioregistrysdk.client.groups.item.artifacts.artifacts_request_builder import ArtifactsRequestBuilder
from apicurioregistrysdk.client.registry_client import RegistryClient
from apicurioregistrysdk.client.models.artifact_content import ArtifactContent

REGISTRY_URL = f"https://registry-external-ref.fly.dev/apis/registry/v2"
# MINIO_URL = "'2a09:8280:1::37:1c21':9000"
MINIO_URL = "registry-external-ref-minio.fly.dev:9000"
BUCKET = "models"

class ModelRegistryClient:
  
  def __init__(self) -> None:
    self.auth_provider = AnonymousAuthenticationProvider()
    self.request_adapter = HttpxRequestAdapter(self.auth_provider)
    self.request_adapter.base_url = REGISTRY_URL
    self.client = RegistryClient(self.request_adapter)
    
    access_key = os.getenv("MINIO_ACCESS_KEY")
    secret_key = os.getenv("MINIO_SECRET_KEY")
    self.s3_client = Minio(
        MINIO_URL,
        access_key=access_key,
        secret_key=secret_key,
        secure=False,
    )
  
  async def register_model(self, name, file_name) -> bool:
    result=self.s3_client.fput_object(BUCKET, name, file_name)
    # print("created {0} object; etag: {1}, version-id: {2}".format(result.object_name, result.etag, result.version_id))
    
    payload = ArtifactContent()
    payload.content = json.dumps({
        "bucket": result.bucket_name,
        "object_name": result.object_name,
        "version": result.version_id,
    })
    config = ArtifactsRequestBuilder.ArtifactsRequestBuilderPostRequestConfiguration()
    config.headers = {
        "x-registry-artifacttype": "EXTREF",
        "X-Registry-Artifactid": name
    }
    try:
        await self.client.groups_by_id("default").artifacts_by_id(name).versions.post(payload, config)
    except:
        await self.client.groups_by_id("default").artifacts.post(payload, config)
    return True
