import matplotlib.pyplot as plt
import numpy as np

errors = [[], [], []]
steps = []
labels = ["Verlet", "Beeman", "Gear Predictor Corrector"]

with open("../damped_error.txt", "r") as error_file:
    line = error_file.readline()
    while line:
        steps.append(float(line))
        for i in range(len(errors)):
            line = error_file.readline()
            errors[i].append(float(line))
        
        error_file.readline()
        line = error_file.readline()

error_file.close()

for i in range(len(errors)):
    plt.plot(steps, errors[i], label=labels[i], marker="o")

plt.xlabel("t (s)", size=14)
plt.ylabel("error ($m^2$)", size=14)

plt.tick_params(labelsize=14)

plt.xscale("log")
plt.yscale("log")

plt.legend()
plt.show()
