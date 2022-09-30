import matplotlib.pyplot as plt
import numpy as np

errors = [[], [], []]
steps = []

# TODO: Capaz promediar y hacer barras de error
with open("../error.txt", "r") as error_file:
    line = error_file.readline()
    while line:
        steps.append(float(line))
        for i in range(len(errors)):
            errors[i].append(float(line))
            line = error_file.readline()
        
        error_file.readline()
        line = error_file.readline()

error_file.close()

for i in range(len(errors)):
    plt.plot(errors[i], label="Error {}".format(i))

plt.xlabel("t (s)")
plt.ylabel("error")
# plt.xticks(np.arange(0, 5.5, step=0.5))
# plt.yticks(np.arange(-1, 1.1, step=0.2))

plt.legend()
plt.show()