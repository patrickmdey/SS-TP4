from matplotlib import pyplot as plt
import math
import numpy as np

min_date = '2023-05-09'
dt = 300

speeds = []

with open("../mission_out.txt", "r") as mission_file:
    line = mission_file.readline()
    while line[:-1] != min_date:
        line = mission_file.readline()
    
    line = mission_file.readline()
    while line[:-1].isnumeric():
        count = int(line)
        mission_file.readline()
        for i in range(count):
            [id, x, y, vx, vy, r] = mission_file.readline().split(",")
            id = int(id)
            if id == 3:
                speeds.append(math.sqrt(float(vx) ** 2 + float(vy) ** 2))

        line = mission_file.readline()


plt.plot(speeds)
xticks = np.arange(0, len(speeds), step=50)
plt.xticks(xticks, [i * dt for i in xticks])
plt.xlabel("Tiempo (s)")
plt.ylabel("Modulo de la Velocidad (km / s)")
plt.show()
