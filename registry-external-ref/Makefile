
build-native:
	mvn clean package -Pnative -Dquarkus.native.container-build=true

docker-native:
	docker build . -t registry-ext-ref
