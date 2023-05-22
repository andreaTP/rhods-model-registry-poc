
# minikube stop && minikube delete && minikube start --driver=docker --memory 8192 --cpus 3

rm -rf modelmesh-serving
git clone -b release-0.10 --depth 1 --single-branch https://github.com/kserve/modelmesh-serving.git
cd modelmesh-serving

kubectl create namespace modelmesh-serving
./scripts/install.sh --namespace-scope-mode --namespace modelmesh-serving --quickstart

