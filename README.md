Compiler
--------

[![Build Status](https://magnum.travis-ci.com/DenBeke/Compiler.svg?token=55DZWEWREsf4wvhULGzt&branch=master)](https://magnum.travis-ci.com/DenBeke/Compiler)


Building
========

    $ make build


Testing
=======

When running `make build` all test are ran immediately. If you wish to run the tests again, do `make test`.

Running individual tests can also be done:

* `make test-grammar`: tests the grammar
* `make test-ast`: tests the AST
* `make test-symboltable`: tests the symbol table


Running
=======

Running the Compiler with custom input:

    $ make run

Or compile an input file:

    $ make run < input.c


Optional features
=================

Right now our AST and symbol table support nested pointer/const declarations. e.g. `const int * const **** a[5]`.

Authors
=======

* Mathias Beke
* Timo Truyts