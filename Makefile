all: build run

build:
	mvn package

run:
	./bin/c2p

test:
	mvn test
	python src/test/python/codegen.py

test-ast:
	mvn test -Dtest=AstTest

test-grammar:
	mvn test -Dtest=GrammarTest

test-symboltable:
	mvn test -Dtest=SymbolTableTest

test-codegen:
	python src/test/python/codegen.py