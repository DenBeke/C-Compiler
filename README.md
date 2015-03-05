Compiler
--------

[![Build Status](https://magnum.travis-ci.com/DenBeke/Compiler.svg?token=55DZWEWREsf4wvhULGzt&branch=master)](https://magnum.travis-ci.com/DenBeke/Compiler)

Running the `TestRig` can be done using maven:

    mvn exec:java -Dexec.mainClass="org.antlr.v4.runtime.misc.TestRig" -Dexec.args="C prog -gui src/test/input/test.txt"


Running the compiler

    mvn exec:java -Dexec.mainClass="Compiler.App"
    