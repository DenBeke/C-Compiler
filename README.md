Compiler
--------

Running the `TestRig` can be done using maven (after you run `mvn package`):

    mvn exec:java -Dexec.mainClass="org.antlr.v4.runtime.misc.TestRig" -Dexec.args="C prog -gui src/test/input/test.txt"