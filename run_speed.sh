speeds=(7.998 7.999 8 8.001 8.002)
rm travel_times.txt

for speed in "${speeds[@]}"
do
    printf "$speed\n" >> travel_times.txt
    /usr/bin/env /Library/Java/JavaVirtualMachines/zulu-19.jdk/Contents/Home/bin/java --enable-preview -XX:+ShowCodeDetailsInExceptionMessages -cp /Users/patrick/Library/Application\ Support/Code/User/workspaceStorage/3d4ed239c984951d8e5c9c58062af416/redhat.java/jdt_ws/SS-TP4_4a5dba8b/bin main.java.ar.edu.itba.ss.VenusVelocityAnalysis $speed
    cd mission-graphics
    python3 travel_time.py
    cd ..
done

cd mission-graphics
python3 plot_travel_times.py
