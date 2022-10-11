from dis import dis
from matplotlib import pyplot as plt
import math
import numpy as np

min_date = '2023-05-08T21:30'
dt = 30000

speeds = []

with open("../sweep_mission_out.txt", "r") as mission_file:
    line = mission_file.readline()
    while line[:-1] != min_date:
        line = mission_file.readline()
    
    line = mission_file.readline()
    flag = True
    while line[:-1].isnumeric():
        count = int(line)
        mission_file.readline()
        for i in range(count):
            [id, x, y, vx, vy, r] = mission_file.readline().split(",")
            id = int(id)
            if id == 3:
                speeds.append(math.sqrt(float(vx) ** 2 + float(vy) ** 2))


        line = mission_file.readline()

mission_file.close()

print((len(speeds)+1) * dt)

plt.plot(speeds)
xticks = np.arange(0, len(speeds), step=10)
plt.xticks(xticks, [i * dt for i in xticks], rotation=45)
plt.xlabel("Tiempo (s)")
plt.ylabel("Modulo de la Velocidad (km / s)")
plt.tight_layout()
plt.show()
