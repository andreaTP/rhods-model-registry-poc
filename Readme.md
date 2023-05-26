# RHODS Model Registry Demo

Agenda:

- Have an Apicurio Registry Docker image with support for(only?) External References - done
- Deploy it to fly.io - done
- Deploy a public S3 equivalent -> Minio // https://fly.io/docs/app-guides/minio/ - done
- Build a Python SDK for uploading the model - published as 'rhods-model-registry-poc' - done
- Invoke the Python SDK from a Jupyther Notebook - connection reset by peer using RHODS - working locally - done
- Have a local Model-mesh installation (based on minikube) - done
- Write a little Operator that handle a CRD "ModelStream" that watches the Registry instance for changes to the latest tag of a model and rewrite the Inference CR for ModelMesh - in progress - done
- deploy modelmesh-serving and the operator in a public K8s offering (free plan) - on Okteto - need to setup too many things, on hold for now
- find a good visualization for the demo - done - good enough
- Wrap it up, blog, have a video or something similar - a python notebook based demo

- extra: deploy a new model from CI


To Do:
- Kiota Registry SDK returns an already closed InputStream - verify
