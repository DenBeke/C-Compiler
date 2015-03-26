all: build run

build:
	mvn package

run:
	mvn exec:java -Dexec.mainClass="Compiler.App"