all: build

build: target/*.jar

target/*.jar: pom.xml $(shell find src -name '*')
	./mvnw package

test:
	./mvnw test

clean:
	rm -rf target
