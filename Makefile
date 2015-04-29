all: build run

build:
	mvn package

run:
	./bin/c2p

test:
	mvn test

test-ast:
	mvn test -Dtest=AstTest

test-grammar:
	mvn test -Dtest=GrammarTest

test-symboltable:
	mvn test -Dtest=SymbolTableTest
