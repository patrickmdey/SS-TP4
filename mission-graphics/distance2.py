from matplotlib import pyplot as plt
import numpy as np

values = []
dates = []
with open("dist_out.txt", "r") as f:
    for line in f:
        parts = line.split(" ")
        values.append(float(parts[-1]))
        dates = parts[0]
f.close()
print("Min: ", min(values))

plt.plot(values)
plt.yscale("log")
xticks = np.arange(0, len(dates), step=40)
plt.xticks(xticks, [dates[i] for i in xticks])
plt.xlabel("Dia de despegue")
plt.ylabel("Distancia Mínima a Venus (km)")
plt.show()
