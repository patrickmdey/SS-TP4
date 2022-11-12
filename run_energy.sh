#!/bin/bash

steps=(900 600 300 150 75)
rm outFiles/energies.txt

for step in "${steps[@]}"
do
    printf "$step\n" >> outFiles/energies.txt
    /usr/bin/env /Library/Java/JavaVirtualMachines/zulu-19.jdk/Contents/Home/bin/java --enable-preview -XX:+ShowCodeDetailsInExceptionMessages -cp /Users/srosati/Library/Application\ Support/Code/User/workspaceStorage/0a9933c281117fa0cddb264610d4b14b/redhat.java/jdt_ws/SS-TP4_3fa4554b/bin main.java.ar.edu.itba.ss.EnergyMain $step
    cd mission-graphics
    python3 energy.py
    cd ..
done

cd mission-graphics
python3 plot_energy.py
