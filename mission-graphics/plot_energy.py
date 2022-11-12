from matplotlib import pyplot as plt


with open("../outFiles/energies.txt", "r") as energies_file:
    line = energies_file.readline()
    
    while line:
        step = int(line)
        line = energies_file.readline()
        energies = []
        while line != "\n":
            energies.append(float(line))
            line = energies_file.readline()

        plt.plot(energies, label="Step {}".format(step))

        line = energies_file.readline()

plt.legend()
plt.show()
