#!/usr/bin/env python3

import asyncio
from model_registry_python_sdk.client import ModelRegistryClient


async def register():
  client = ModelRegistryClient()
  await client.register_model("ci-example.joblib", "example.joblib",
      "my very very complicated model",
      {
        "framework": "scikit-learn",
        "tag": "experimental",
        "creator": "GH Action",
        "link": "https://huggingface.co/NimaBoscarino/evaluate-test"
      }, ["staging", "validate", "auto", "ci"])

if __name__ == "__main__":
  asyncio.run(register())
