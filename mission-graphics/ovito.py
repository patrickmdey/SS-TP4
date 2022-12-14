with open("../outFiles/e_mission_out.txt", "r") as mission_file:
    with open("../outFiles/ovito.txt", "w") as ovito_file:
        line = mission_file.readline()
        while line:
            if not line[:-1].isnumeric():
                if line[:-1] != "2025-01-10":
                    line = mission_file.readline()
                else:
                    print("Found 2025-01-10")
                    line = mission_file.readline()
                    while line[:-1].isnumeric():
                        count = int(line)
                        mission_file.readline()
                        ovito_file.write("{}\n\n".format(count))
                        for i in range(count):
                            [id, x, y, vx, vy, r, _] = mission_file.readline().split(",")

                            if i == 0:
                                scalated_r = float(r)
                            elif i == 3:
                                scalated_r = 70000
                            else:
                                scalated_r = float(r) * 35

                            x = float(x)/15
                            y = float(y)/15

                            ovito_file.write("{}, {}, {}, {}, {}, {}\n".format(id, x, y, vx, vy, scalated_r))
                        line = mission_file.readline()
            else:
                line = mission_file.readline()

    ovito_file.close()

mission_file.close()

