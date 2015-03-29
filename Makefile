all: build run

build:
	mvn package

run:
	mvn exec:java -Dexec.mainClass="Compiler.App"

test-all:
	mvn test

test-ast:
	mvn test -Dtest=AstTest

test-grammar:
	mvn test -Dtest=GrammarTest

test-symboltable:
	mvn test -Dtest=SymbolTableTest
