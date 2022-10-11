speeds=(7.998 7.999 8 8.001 8.002)
rm travel_times.txt

for speed in "${speeds[@]}"
do
    printf "$speed\n" >> travel_times.txt
    /usr/bin/env /Library/Java/JavaVirtualMachines/zulu-19.jdk/Contents/Home/bin/java --enable-preview -XX:+ShowCodeDetailsInExceptionMessages -cp /Users/srosati/Library/Application\ Support/Code/User/workspaceStorage/0a9933c281117fa0cddb264610d4b14b/redhat.java/jdt_ws/SS-TP4_3fa4554b/bin main.java.ar.edu.itba.ss.VenusVelocityAnalysis $speed
    cd mission-graphics
    python3 travel_time.py
    cd ..
done

cd mission-graphics
python3 plot_travel_times.py
