from matplotlib import pyplot as plt

times = []
speeds = []

with open("../travel_times.txt", "r") as time_file:
    line = time_file.readline()
    while line:
        speeds.append(line[:-1])
        line = time_file.readline()
        times.append(int(line))
        line = time_file.readline()

time_file.close()

plt.plot(speeds, times, marker="o")
plt.yscale("log")
plt.ylabel("Tiempo de viaje (s)", size=14)
plt.xlabel("Velocidad Inicial (km/s)", size=14)
plt.tick_params(labelsize=14)
plt.show()
