#!/bin/sh
cd build;
mkdir -p bin;
make clean;
make;
cd ..;
for i in {1..1}; do 
	echo "run " $i;
	./build/bin/rtts; 
done