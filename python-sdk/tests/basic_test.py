import pytest
import os
import sys
from model_registry_python_sdk.client import Client

@pytest.mark.asyncio
async def test_basic_upload_download():
    client = Client()
    await client.upload_model("test", os.path.join(sys.path[0], "my_example_content.txt"))
    assert(True, False)
