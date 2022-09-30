import matplotlib.pyplot as plt
import numpy as np

errors = [[], [], []]
steps = []
labels = ["Verlet", "Beeman", "Gear Predictor Corrector"]

with open("../error.txt", "r") as error_file:
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
    plt.plot(steps, errors[i], label=labels[i])

plt.xlabel("t (s)")
plt.ylabel("error")

plt.xscale("log")
plt.yscale("log")

plt.legend()
plt.show()
