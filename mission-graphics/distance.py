from matplotlib import pyplot as plt
import math
import numpy as np

def distance(x1, y1, r1, x2, y2, r2):
    return math.sqrt((x1 - x2) ** 2 + (y1 - y2) ** 2) - r1 - r2

date_idx = -1
start_dates = []
min_dist = []

with open("../mission_out.txt", "r") as mission_file:
    line = mission_file.readline()
    
    while line:
        if not line[:-1].isnumeric():
            date_idx += 1
            min_dist.append(None)
            start_dates.append(line[:-1])
            line = mission_file.readline()
        
        count = int(line)
        mission_file.readline()
        bodies_info = np.empty((count, 3))
        for i in range(count):
            [id, x, y, vx, vy, r] = mission_file.readline().split(",")
            id = int(id)
            bodies_info[id] = [float(x), float(y), float(r)]

        # Calculate distance between spaceship and venus
        dist = distance(bodies_info[2][0], bodies_info[2][1], bodies_info[2][2], bodies_info[3][0], bodies_info[3][1], bodies_info[3][2])
        if min_dist[date_idx] == None or dist < min_dist[date_idx]:
            min_dist[date_idx] = dist
        
        line = mission_file.readline()


abs_min = min(min_dist)
min_dist_date = start_dates[min_dist.index(abs_min)]
print("Min Distance: {}".format(abs_min))
print("Min Distance Date: {}".format(min_dist_date))

plt.plot(min_dist)
xticks = np.arange(0, len(start_dates), step=40)
plt.xticks(xticks, [start_dates[i] for i in xticks])
plt.xlabel("Dia de despuegue")
plt.ylabel("Distancia Mínima a Venus (km)")
plt.show()
