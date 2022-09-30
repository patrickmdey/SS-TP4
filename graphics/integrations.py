import matplotlib.pyplot as plt
import numpy as np

values = [[], [], [], []]

with open("../out.txt", "r") as out_file:
    idx = 0
    for line in out_file:
        if line == '\n':
            idx += 1
            continue

        parts = [float(n) for n in line.split()]
        if len(parts) == 0:
            continue

        values[idx].append(parts)
out_file.close()

labels = ["Analitic", "Verlet", "Beeman", "Gear Predictor Corrector"]
styles = ['solid', 'dashed', 'dashdot', 'dotted']
colors = ['red', 'blue', 'green', 'pink']

analitic_y = [n[1] for n in values[0]]
for i in range(len(labels)):
    x = [n[0] for n in values[i]]
    y = [n[1] for n in values[i]]
    plt.plot(x, y, label=labels[i], linestyle=styles[i], c=colors[i])

plt.xlabel("t (s)")
plt.ylabel("r (m)")
plt.xticks(np.arange(0, 5.5, step=0.5))
plt.yticks(np.arange(-1, 1.1, step=0.2))
plt.legend()

plt.show()


