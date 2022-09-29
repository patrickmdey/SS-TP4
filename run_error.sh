#!/bin/bash

steps=(0.1 0.05 0.01 0.001)

rm error.txt
for t in ${steps[@]}; do
	printf $t >> error.txt
	printf "\n" >> error.txt
    ./run.sh $t
    cd graphics
    python3 error.py
    cd ..
done

cd graphics
python3 plot_errors.py