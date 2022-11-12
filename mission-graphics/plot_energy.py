from matplotlib import pyplot as plt
import numpy as np

with open("../outFiles/energies.txt", "r") as energies_file:
    line = energies_file.readline()
    
    while line:
        step = int(line)
        x_step = step
        line = energies_file.readline()
        energies = []
        while line != "\n":
            energies.append(float(line))
            line = energies_file.readline()

        x_vals = np.arange(0, x_step * len(energies), x_step) / (60 * 60 * 24)
        plt.plot(x_vals, energies, label="Step {}".format(step))

        line = energies_file.readline()


plt.legend()
plt.show()
