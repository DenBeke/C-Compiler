C Compiler
----------

[![Build Status](https://magnum.travis-ci.com/DenBeke/Compiler.svg?token=55DZWEWREsf4wvhULGzt&branch=master)](https://magnum.travis-ci.com/DenBeke/Compiler)

C compiler that compiles a subset of C. The compiler, written in Java using ANTLR, compiles to the [Pmachine](http://ansymore.uantwerpen.be/sites/ansymo.ua.ac.be/files/uploads/courses/Compilers/pMachine/index.html).

Building
========

Build the C compiler:

    $ make build

Download and build the Pmachine:

    $ ./install_pmachine.sh


Testing
=======

When running `make build` all test are ran immediately, except for the code generation tests. If you wish to run the tests again, do `make test`.  
*[Pmachine](http://ansymore.uantwerpen.be/sites/ansymo.ua.ac.be/files/uploads/courses/Compilers/pMachine/index.html) must be compiled in `Pmachine` directory for `test-codegen`: `./install_pmachine.sh`*  
*`test-codegen` requires the `python2` executable.*  

Running individual tests can also be done:

* `make test-grammar`: tests the grammar
* `make test-ast`: tests the AST
* `make test-symboltable`: tests the symbol table
* `make test-codegen`: code generation scenarios


**Snake**  
To demonstrate the compiler, we have added a small snake demo C file `demo-snake.c` which you can compile and run.


Running
=======

Running the Compiler with custom manual input:

    $ ./bin/c2p > compiled_file.p


Or compile an input file:

    $ ./bin/c2p < my_file.c > compiled_file.p
    

Run the compiled code on the Pmachine:

    $ ./Pmachine/Pmachine compiled_file.p 


Optional features
=================

* Support for nested pointer/const declarations. e.g. `const int * const **** a[5]`
* Pointer arithmetic
* Implicit casts
* Cast operator
* Error when calling undeclared functions
* Error when using undeclared variables
* Error when parameters for a function call don't match
* Checking for presence of return statements in all branches of function
* Forward declarations
* Nested functions including proper scoping
* Warning when over initializing arrays
* Support for some built-in functions (when including `stdio.h`):
    * `void printf(char*, ...);`
    * `void print(char*);`
    * `int strcmp(char*, char*);`
    * `void scanf(char*, ...);`
    * `int isdigit(char);`
    * `int pow(int, int);`
    * `int chartoint(char);`
    * `void readstr(char*);`
    * `int mod(int, int);`
    * `void reverse(char*);`
    * `int strlen(char*);`
    * `int atoi(char*);`
    * `void itoa(int, char*);`

Authors
=======

* [Mathias Beke](http://denbeke.be)
* tnt
