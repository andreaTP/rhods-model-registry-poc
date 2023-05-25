# RHODS Model Registry Demo

Agenda:

- Have an Apicurio Registry Docker image with support for(only?) External References - done
- Deploy it to fly.io - done
- Deploy a public S3 equivalent -> Minio // https://fly.io/docs/app-guides/minio/ - done
- Build a Python SDK for uploading the model - published as 'rhods-model-registry-poc' - done
- Invoke the Python SDK from a Jupyther Notebook - connection reset by peer using RHODS - working locally - done
- Have a local Model-mesh installation (based on minikube) - done
- Write a little Operator that handle a CRD "ModelStream" that watches the Registry instance for changes to the latest tag of a model and rewrite the Inference CR for ModelMesh - in progress - done
- find a good visualization for the demo
- Wrap it up, blog, have a video or something similar

- extra: deploy a new model from CI


To Do:
- registry publish sql sub-module - TODO
- python-sdk registry -> skip generation if file exists - submit PR
- Kiota Registry SDK returns an already closed InputStream - verify
