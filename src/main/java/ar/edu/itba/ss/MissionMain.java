package main.java.ar.edu.itba.ss;

import main.java.ar.edu.itba.ss.VenusMission.models.CelestialBody;
import main.java.ar.edu.itba.ss.VenusMission.models.Point;
import main.java.ar.edu.itba.ss.utils.IntegrationAlgorithms;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;


public class MissionMain {

    //    public static final double[] ALPHAS = new double[]{3.0 / 16, 251.0 / 360, 1, 11.0 / 18, 1.0 / 6, 1.0 / 60};
    public static final double[] ALPHAS = new double[]{3.0 / 20, 251.0 / 360, 1, 11.0 / 18, 1.0 / 6, 1.0 / 60};

    public static void main(String[] args) {

        CelestialBody sun, earth, venus;
        long minIter = Integer.MAX_VALUE;//695700
        sun = new CelestialBody(0, "Sun", new Point(0, 0), 0, 0, 695_700, 1_988_500 * Math.pow(10, 24), 0);

        double takeOffSpeed = 8;
        double earthOrbitSpeed = 7.12;

        double earthX = 0, earthY = 0, earthVx = 0, earthVy = 0;
        double venusX = 0, venusY = 0, venusVx = 0, venusVy = 0;

        try (Scanner earthScanner = new Scanner(new File("src/main/java/ar/edu/itba/ss/files/earth-horizons.csv"));
             Scanner venusScanner = new Scanner(new File("src/main/java/ar/edu/itba/ss/files/venus-horizons.csv"))) {

            earthScanner.nextLine();
            venusScanner.nextLine();
            //Read line
            while (earthScanner.hasNextLine() && venusScanner.hasNextLine()) {
                String earthLine = earthScanner.nextLine();
                String venusLine = venusScanner.nextLine();
                //Scan the line for tokens
                try (Scanner earthRowScanner = new Scanner(earthLine); Scanner venusRowScanner = new Scanner(venusLine)) {
                    earthRowScanner.useDelimiter(",");
                    venusRowScanner.useDelimiter(",");
                    for (int i = 0; earthRowScanner.hasNext() && venusRowScanner.hasNext(); i++) {
                        String earthPart = earthRowScanner.next();
                        String venusPart = venusRowScanner.next();

                        switch (i) {
                            case 2: {
                                earthX = Double.parseDouble(earthPart);
                                venusX = Double.parseDouble(venusPart);
                                break;
                            }
                            case 3: {
                                earthY = Double.parseDouble(earthPart);
                                venusY = Double.parseDouble(venusPart);
                                break;
                            }
                            case 5: {
                                earthVx = Double.parseDouble(earthPart);
                                venusVx = Double.parseDouble(venusPart);
                                break;
                            }
                            case 6: {
                                earthVy = Double.parseDouble(earthPart);
                                venusVy = Double.parseDouble(venusPart);
                                break;
                            }
                        }
                    }
                }
                earth = new CelestialBody(1, "Earth", new Point(earthX, earthY), earthVx, earthVy,
                        6_371.01, 5.97219 * Math.pow(10, 24), 29.79);
                venus = new CelestialBody(2, "Venus", new Point(venusX, venusY), venusVx, venusVy,
                        6_051.84, 48.685 * Math.pow(10, 23), 35.021);


                double spaceshipOrbitRadius = earth.getRadius() + 1500;

                double earthDistanceToSun = earth.getPosition().distanceTo(sun.getPosition());


                // esta del lado izquierdo
                double spaceshipDistanceToSun = earthDistanceToSun - spaceshipOrbitRadius;

                double theta = Math.atan2(earth.getPosition().getY(), earth.getPosition().getX());

                // Pasamos de normal a cartesiano
                double x = Math.cos(theta) * spaceshipDistanceToSun;
                double y = Math.sin(theta) * spaceshipDistanceToSun;

                // Pasamos de tangencial a cartesiano
                double vx = (Math.signum(earth.getVx()) * Math.abs(Math.sin(theta)
                        * (earthOrbitSpeed + takeOffSpeed))) + earth.getVx();
                double vy = -(Math.signum(earth.getVy()) * Math.abs(Math.cos(theta)
                        * (earthOrbitSpeed + takeOffSpeed))) + earth.getVy();

                CelestialBody spaceship = new CelestialBody(3, "Spaceship", new Point(x, y), vx,
                        vy, 0, 2 * Math.pow(10, 5), 0);

                List<CelestialBody> celestialBodies = new ArrayList<>(Arrays.asList(sun, earth, venus, spaceship));
                long iter = 0;

                double[][] rx = new double[4][6];
                double[][] ry = new double[4][6];

                initializeRs(rx, ry, celestialBodies);

                try (FileWriter outFile = new FileWriter("mission_out.txt", true)) {
                    while (venus.getPosition().distanceTo(spaceship.getPosition()) > venus.getRadius() +
                            spaceship.getRadius() + 1200 && iter < 1000000) {
                        if (iter % 10 == 0) {
                            outFile.write("4\n\n");
                            outFile.write(String.format(Locale.ROOT, "%d, %.16f, %.16f, %.16f, %.16f, %.16f\n", sun.getId(),
                                    sun.getPosition().getX(), sun.getPosition().getY(), sun.getVx(), sun.getVy(), sun.getRadius()));
                        }

                        int step = 60 * 60;

                        // Predecir
                        for (int i = 1; i < celestialBodies.size(); i++) {
                            CelestialBody body = celestialBodies.get(i);
                            IntegrationAlgorithms.gearPredR(rx[i], step);
                            IntegrationAlgorithms.gearPredR(ry[i], step);

                            body.getPosition().update(rx[i][0], ry[i][0]); //Son las predicciones
                            body.updateVelocity(rx[i][1], ry[i][1]);
                        }

                        double[] deltaR2x = new double[4];
                        double[] deltaR2y = new double[4];

                        // Evaluar
                        for (int i = 1; i < celestialBodies.size(); i++) {
                            CelestialBody body = celestialBodies.get(i);
                            double[] predF = body.totalGravitationalForces(celestialBodies.stream()
                                    .filter(b -> b != body).collect(Collectors.toList()));

                            double currAx = predF[0] / body.getMass();
                            double currAy = predF[1] / body.getMass();

                            double deltaAx = currAx - rx[i][2];
                            double deltaAy = currAy - ry[i][2];
                            deltaR2x[i] = deltaAx * Math.pow(step, 2) / 2;
                            deltaR2y[i] = deltaAy * Math.pow(step, 2) / 2;
                        }


                        // Corregir
                        for (int i = 1; i < celestialBodies.size(); i++) {
                            IntegrationAlgorithms.gearCorrectR(ALPHAS, rx[i], deltaR2x[i], step);
                            IntegrationAlgorithms.gearCorrectR(ALPHAS, ry[i], deltaR2y[i], step);

                            CelestialBody body = celestialBodies.get(i);
                            body.getPosition().update(rx[i][0], ry[i][0]);
                            body.updateVelocity(rx[i][1], ry[i][1]);
                            if (iter % 10 == 0)
                                outFile.write(String.format(Locale.ROOT, "%d, %.16f, %.16f, %.16f, " +
                                        "%.16f, %.16f\n", body.getId(), body.getPosition().getX(),
                                        body.getPosition().getY(), body.getVx(), body.getVy(),
                                        body.getRadius()));
                        }

                        outFile.flush();
                        iter++;
                    }

//                    outFile.write("\n"); // TODO: Ver como separamos condiciones iniciales

                    if (iter < minIter) minIter = iter;
                } catch (IOException e) {
                    e.printStackTrace();
                    System.exit(1);
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            System.exit(1);
        }

        System.out.println(minIter);
    }

    public static void initializeRs(double[][] rx, double[][] ry, List<CelestialBody> celestialBodies) {
        for (int i = 1; i < celestialBodies.size(); i++) {
            CelestialBody body = celestialBodies.get(i);
            rx[i][0] = body.getPosition().getX();
            ry[i][0] = body.getPosition().getY();

            rx[i][1] = body.getVx();
            ry[i][1] = body.getVy();

            double[] forces = body.totalGravitationalForces(celestialBodies.stream().filter(b -> b != body).collect(Collectors.toList()));

            rx[i][2] = forces[0] / body.getMass();
            ry[i][2] = forces[1] / body.getMass();

            rx[i][3] = 0;
            ry[i][3] = 0;

            rx[i][4] = 0;
            ry[i][4] = 0;

            rx[i][5] = 0;
            ry[i][5] = 0;
        }
    }
}
