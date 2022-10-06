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
                    scalated_r = float(r) * 1.6
                elif i == 3:
                    scalated_r = 180000
                else:
                    scalated_r = float(r) * 110

                x = float(x)/15
                y = float(y)/15

                ovito_file.write("{}, {}, {}, {}, {}, {}\n".format(id, x, y, vx, vy, scalated_r))
            line = mission_file.readline()

    ovito_file.close()

mission_file.close()

