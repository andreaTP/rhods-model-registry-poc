# RHODS Model Registry Demo

Agenda:

- Have an Apicurio Registry Docker image with support for(only?) External References - done
- Deploy it to fly.io - done
- Deploy a public S3 equivalent -> Minio // https://fly.io/docs/app-guides/minio/ - done
- Build a Python SDK for uploading the model - in-progress
- Invoke the Python SDK from a Jupyther Notebook
- Have a local Model-mesh installation (based on minikube)
- Write a little Operator that handle a CRD "ModelStream" that watches the Registry instance for changes to the latest tag of a model and rewrite the Inference CR for ModelMesh
- Wrap it up, blog, have a video or something similar

- extra: deploy a new model from CI
