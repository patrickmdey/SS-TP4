import numpy as np

from matplotlib import pyplot as plt

G = 6.693 * pow(10, -20)

energies = []
with open("../outFiles/energy_out.txt", "r") as mission_file:
    line = mission_file.readline()
    particles = np.zeros(shape=(4, 3))

    while line:

        mission_file.readline()

        energy = 0
        for i in range(4):
            [id, x, y, vx, vy, r, m] = mission_file.readline().split(",")

            energy += 0.5 * float(m) * (float(vx) ** 2 + float(vy) ** 2)
            particles[i] = (float(x), float(y), float(m))

        for i in range(4):
            (x1, y1, m1) = particles[i]
            for j in range(i + 1, 4):
                (x2, y2, m2) = particles[j]

                dist = np.sqrt((x1 - x2) ** 2 + (y1 - y2) ** 2)
                energy += (-G) * m1 * m2 / dist

        energies.append(energy)
        line = mission_file.readline()

mission_file.close()

with open("../outFiles/energies.txt", "a") as energies_out:
    for i in range(1,len(energies)):
        energies_out.write("{}\n".format(100 * abs((energies[i] - energies[0]) / energies[0])))

    energies_out.write("\n")

energies_out.close()
