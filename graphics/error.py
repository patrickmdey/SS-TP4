import numpy as np

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


reference = np.array(values[0])

with open("../error.txt", "a") as error_file:
    for i in range(1, len(values)):
        # Mean Quadratic Error
        error = np.mean(np.square(reference - np.array(values[i])))
        error_file.write(str(error) + "\n")
    
    error_file.write("\n")

error_file.close()

