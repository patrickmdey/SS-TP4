import matplotlib.pyplot as plt

values = [[], [], [], []]

with open("../out.txt", "r") as out_file:
    idx = 0
    for line in out_file:
        if line == '\n':
            idx += 1
            print("idx: ", idx)
            continue

        parts = [float(n) for n in line.split()]
        if len(parts) == 0:
            continue

        print(parts)
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

plt.legend()
plt.show()


