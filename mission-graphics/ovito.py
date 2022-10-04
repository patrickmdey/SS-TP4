with open("../mission_out.txt", "r") as mission_file:
    with open("../ovito.txt", "w") as ovito_file:
        line = mission_file.readline()
        while line:
            count = int(line)
            mission_file.readline()
            ovito_file.write("{}\n\n".format(count))
            for i in range(count):
                [id, x, y, vx, vy, r] = mission_file.readline().split(",")
                if i == 0:
                    r = float(r) * 1000
                else:
                    r = float(r) * 100000

                x = float(x) / 1000
                y = float(y) / 1000

                ovito_file.write("{}, {}, {}, {}, {}, {}\n".format(id, x, y, vx, vy, r))
            line = mission_file.readline()

    ovito_file.close()

mission_file.close()

