from dis import dis
from matplotlib import pyplot as plt
import math
import numpy as np

min_date = '2023-05-08T21:30'
dt = 300

steps_per_day = math.ceil(24 * 60 * 60 / dt)
print("Steps per day:", steps_per_day)
print()

total_days = 0
speeds = []
day_steps = 1
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

        if (day_steps % steps_per_day == 0):
            total_days += 1
            day_steps = 0
        
        day_steps += 1
        
        line = mission_file.readline()

mission_file.close()


time_elapsed = dt * day_steps

hours = int(time_elapsed / 3600)
minutes = int((time_elapsed - hours * 3600) / 60)

# ESTO SOLO DA BIEN SI NO SE SALTEAN ITERACIONES AL ESCRIBIR, SINO VA A DAR SIEMPRE UN POCO DE MAS
print(total_days, hours, minutes) 

plt.plot(speeds)
xticks = np.arange(0, len(speeds), step=int(len(speeds) / 20))
plt.xticks(xticks, [i * dt for i in xticks], rotation=45)
plt.xlabel("Tiempo (s)", size=14)
plt.ylabel("Modulo de la Velocidad (km / s)", size=14)
plt.tick_params(labelsize=14)
plt.tight_layout()
plt.show()
