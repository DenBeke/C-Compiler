Compiler
--------

[![Build Status](https://magnum.travis-ci.com/DenBeke/Compiler.svg?token=55DZWEWREsf4wvhULGzt&branch=master)](https://magnum.travis-ci.com/DenBeke/Compiler)


Building
========

    $ make build


Testing
=======

When running `make build` all test are ran immediately, except for the code generation tests. If you wish to run the tests again, do `make test`.  
*[Pmachine](http://ansymore.uantwerpen.be/sites/ansymo.ua.ac.be/files/uploads/courses/Compilers/pMachine/index.html) must be compiled in `Pmachine` directory for `test-codegen`: `./install_pmachine.sh`*  
Running individual tests can also be done:

* `make test-grammar`: tests the grammar
* `make test-ast`: tests the AST
* `make test-symboltable`: tests the symbol table
* `make test-codegen`: code generation scenarios


Running
=======

Running the Compiler with custom input:

    $ make run

Or compile an input file:

    $ make run < input.c


Optional features
=================

* Right now our AST and symbol table support nested pointer/const declarations. e.g. `const int * const **** a[5]`.
* Implicit casts
* Cast operator
* Error when calling undeclared functions
* Checking for presence of return statements in all branches of function

Authors
=======

* Mathias Beke
* Timo Truyts