#!/bin/sh
cd build;
make clean;
make;
cd ..;
for i in {1..1}; do 
	echo "run " $i;
	./build/bin/rtts; 
done