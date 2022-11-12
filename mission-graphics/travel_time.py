import numpy as np
import math

def distance(x1, y1, r1, x2, y2, r2):
    return math.sqrt((x1 - x2) ** 2 + (y1 - y2) ** 2) - r1 - r2

dt = 300
time = dt # La posicion inicial nunca se escribe
with open("../outFiles/v_mission_out.txt", "r") as file:
    line = file.readline()
    while line:
        count = int(line)
        file.readline()
        bodies_info = np.empty((count, 3))
        for i in range(count):
            file.readline().split(",")

        line = file.readline()
        time += dt

file.close()

with open("../travel_times.txt", "a") as time_file:
    time_file.write(str(time) + "\n")

time_file.close()
