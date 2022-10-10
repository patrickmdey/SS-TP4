from datetime import date
from matplotlib import pyplot as plt
import numpy as np

values = []
dates = []
with open("../distance_out.txt", "r") as f:
    for line in f:
        parts = line.split(" ")
        values.append(float(parts[-1][:-1]))
        dates.append(parts[0])
f.close()

plt.plot(values)
plt.yscale("log")
xticks = np.arange(0, len(dates), step=34)
plt.xticks(xticks, [dates[i] for i in xticks], rotation=45)
plt.xlabel("Dia de despegue")
plt.ylabel("Distancia Mínima a Venus (km)")
plt.show()
