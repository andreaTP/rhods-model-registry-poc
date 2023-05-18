
build-native:
	mvn clean package -Pnative -Dquarkus.native.container-build=true

docker-native:
	docker build . -t andreatp/registry-external-ref:latest

push-docker:
	docker push andreatp/registry-external-ref:latest
