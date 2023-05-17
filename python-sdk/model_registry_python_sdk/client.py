import os
from minio import Minio

from kiota_abstractions.authentication.anonymous_authentication_provider import (
    AnonymousAuthenticationProvider,
)
from kiota_http.httpx_request_adapter import HttpxRequestAdapter

TODO -> import is not working!

from apicurio_registry_python_sdk_preview.python_sdk.client.registry_client import RegistryClient
# from python_sdk.client.models.artifact_content import ArtifactContent
# from python_sdk.client.groups.item.artifacts.artifacts_request_builder import ArtifactsRequestBuilder

REGISTRY_URL = f"https://registry-external-ref.fly.dev/apis/registry/v2"
MINIO_URL = "registry-external-ref-minio.fly.dev:9000"
BUCKET = "test-bucket"

class Client:
  
  def __init__(self) -> None:
    self.auth_provider = AnonymousAuthenticationProvider()
    self.request_adapter = HttpxRequestAdapter(self.auth_provider)
    self.request_adapter.base_url = REGISTRY_URL
    # self.client = RegistryClient(self.request_adapter)
    
    access_key = os.getenv("MINIO_ACCESS_KEY")
    secret_key = os.getenv("MINIO_SECRET_KEY")
    self.s3_client = Minio(
        MINIO_URL,
        access_key=access_key,
        secret_key=secret_key,
        secure=False,
    )
  
  async def upload_model(self, name, file_name) -> bool:
    result=self.s3_client.fput_object(BUCKET, name, file_name)
    print("created {0} object; etag: {1}, version-id: {2}".format(result.object_name, result.etag, result.version_id))
    
    # payload = ArtifactContent()
    # payload.content = "TODO-reference-to-s3"
    # config = ArtifactsRequestBuilder.ArtifactsRequestBuilderPostRequestConfiguration()
    # config.headers = { "x-registry-artifacttype": "EXTREF" }
    # create_artifact = await self.client.groups_by_id("default").artifacts.post(payload, config)
    return True
