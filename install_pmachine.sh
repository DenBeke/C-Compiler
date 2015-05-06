#!/bin/sh

curl http://ansymore.uantwerpen.be/sites/ansymo.ua.ac.be/files/uploads/courses/Compilers/pMachine/Pmachine.tar.gz > pmachine.tgz
tar -xvzf pmachine.tgz
cd Pmachine/
make clean
make
cd ..