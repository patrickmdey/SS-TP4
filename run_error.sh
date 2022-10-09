#!/bin/bash

steps=(0.003 0.01 0.001 0.0003 0.0001 0.00003 0.00001 0.000003 0.000001)

rm damped_error.txt
for t in ${steps[@]}; do
	printf $t >> damped_error.txt
	printf "\n" >> damped_error.txt
    ./run.sh $t
    cd graphics
    python3 error.py
    cd ..
done

cd graphics
python3 plot_errors.py
