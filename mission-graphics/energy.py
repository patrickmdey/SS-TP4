import numpy as np

from matplotlib import pyplot as plt

G = 6.693e-11

energies = []
with open("../outFiles/mission_out.txt", "r") as mission_file:
    line = mission_file.readline()
    while line:
        if not line[:-1].isnumeric():
            line = mission_file.readline()

        count = int(line)
        mission_file.readline()
        particles = []

        energy = 0
        for i in range(count):
            [id, x, y, vx, vy, r, m] = mission_file.readline().split(",")

            energy += 0.5 * float(m) * (float(vx) ** 2 + float(vy) ** 2)
            particles.add((float(x), float(y), float(m)))

        for i in range(count):
            (x1, y1, m1) = particles[i]
            for j in range(i + 1, count):
                (x2, y2, m2) = particles[j]

                dist = np.sqrt((x1 - x2) ** 2 + (y1 - y2) ** 2)
                energy += 2 * (-G) * m1 * m2 / dist # TODO: Checkear -G

        energies.append(energy)
        line = mission_file.readline()


mission_file.close()

plt.plot(energies, marker="o")
plt.show()