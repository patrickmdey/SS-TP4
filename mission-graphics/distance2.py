from datetime import date
from matplotlib import pyplot as plt
import numpy as np

values = []
dates = []
with open("../sweep_distance_out.txt", "r") as f:
    for line in f:
        parts = line.split(",")
        values.append(float(parts[-1][:-1]))
        dates.append(parts[0])
f.close()

print("Landing time:" + dates[values.index(min(values))])
print("Minimum distance:" + str(min(values)))
plt.plot(values)
plt.yscale("symlog")
xticks = np.arange(0, len(dates), step=34)
plt.xticks(xticks, [dates[i] for i in xticks], rotation=45)
plt.ylim((min(values), None))
plt.tight_layout()
plt.xlabel("Dia de despegue")
plt.ylabel("Distancia Mínima a Marte (km)")
plt.show()
