from analytic import calculateR

values = [[], [], []]

with open("../out.txt", "r") as out_file:
    idx = 0
    for line in out_file:
        if line == '\n':
            idx += 1
            continue

        parts = [float(n) for n in line.split()]
        if len(parts) == 0:
            continue
        values[idx].append(parts)

out_file.close()

analytic = [calculateR(t[0]) for t in values[0]]

with open("../error.txt", "a") as error_file:
    for i in range(len(values)):
        # Mean Quadratic Error
        error = 0
        for j in range(len(analytic)):
            error += (analytic[j] - values[i][j][1]) ** 2
        
        error /= len(analytic)
        error_file.write(str(error) + "\n")
    
    error_file.write("\n")

error_file.close()

