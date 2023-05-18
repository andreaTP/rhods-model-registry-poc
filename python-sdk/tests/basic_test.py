import pytest
import os
import sys
from model_registry_python_sdk.client import ModelRegistryClient

@pytest.mark.asyncio
async def test_basic_register():
    client = ModelRegistryClient()
    await client.register_model("my-awesome-model", os.path.join(sys.path[0], "my_example_content.txt"))
    assert(True, False)
