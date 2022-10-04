package main.java.ar.edu.itba.ss;

import javafx.geometry.Pos;
import main.java.ar.edu.itba.ss.VenusMission.models.CelestialBody;
import main.java.ar.edu.itba.ss.VenusMission.models.Point;
import main.java.ar.edu.itba.ss.utils.IntegrationAlgorithms;

import javax.swing.text.Position;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;


public class MissionMain {

    public static final double[] ALPHAS = new double[]{3.0 / 16, 251.0 / 360, 1, 11.0 / 18, 1.0 / 6, 1.0 / 60};

    public static void main(String[] args) {
        CelestialBody sun = null, earth = null, venus = null;
        long minIter = Integer.MAX_VALUE;
        sun = new CelestialBody("Sun", new Point(0, 0), 0, 0, 6.9551 * Math.pow(10, 5), 1988500 * Math.pow(10, 24), 0);

        double earthX = 0, earthY = 0, earthVx = 0, earthVy = 0;
        double venusX = 0, venusY = 0, venusVx = 0, venusVy = 0;
        try (Scanner earthScanner = new Scanner(new File("src/main/java/ar/edu/itba/ss/files/earth-horizons.csv"));
             Scanner venusScanner = new Scanner(new File("src/main/java/ar/edu/itba/ss/files/venus-horizons.csv"))) {

            earthScanner.nextLine();
            venusScanner.nextLine();
            //Read line
            while (earthScanner.hasNextLine() && venusScanner.hasNextLine()) {
                String line = earthScanner.nextLine();
                String venusLine = earthScanner.nextLine();
                //Scan the line for tokens
                try (Scanner earthRowScanner = new Scanner(line); Scanner venusRowScanner = new Scanner(venusLine)) {
                    earthRowScanner.useDelimiter(",");
                    venusRowScanner.useDelimiter(",");
                    for (int i = 0; earthRowScanner.hasNext(); i++) {
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
                earth = new CelestialBody("Earth", new Point(earthX, earthY), earthVx, earthVy,
                        6378.137, 5.97219 * Math.pow(10, 24), 29.79);
                venus = new CelestialBody("Venus", new Point(venusX, venusY), venusVx, venusVy,
                        6051.893, 48.685 * Math.pow(10, 23), 35.021);


                double spaceshipOrbitRadius = earth.getRadius() + 1500;

                double earthDistanceToSun = earth.getPosition().distanceTo(sun.getPosition());

                // esta del lado izquierdo
                double spaceshipDistanceToSun = earthDistanceToSun - spaceshipOrbitRadius;

                double theta = Math.atan2(earth.getPosition().getY(), earth.getPosition().getX());

                // Pasamos de normal a cartesiano
                double x = Math.cos(theta) * spaceshipDistanceToSun;
                double y = Math.sin(theta) * spaceshipDistanceToSun;

                // TODO: ta hardcodeado
                // Pasamos de tangencial a cartesiano
                double vx = (Math.signum(earth.getVx()) * Math.abs(Math.sin(theta) * (7.12 + 8))) + earth.getVx();
                double vy = (Math.signum(earth.getVy()) * Math.abs(Math.cos(theta) * (7.12 + 8))) + earth.getVy();

                CelestialBody spaceship = new CelestialBody("Spaceship", new Point(x, y), vx, vy, 0,
                        2 * Math.pow(10, 5), 0);

                List<CelestialBody> celestialBodies = new ArrayList<>(Arrays.asList(sun, earth, venus, spaceship));
                long iter = 0;

                double[][] rx = new double[4][6];
                double[][] ry = new double[4][6];

                for (int i = 0; i < celestialBodies.size(); i++) {

                    CelestialBody body = celestialBodies.get(i);
                    rx[i][0] = body.getPosition().getX();
                    ry[i][0] = body.getPosition().getY();

                    rx[i][1] = body.getVx();
                    ry[i][1] = body.getVy();

                    rx[i][2] = 0;
                    ry[i][2] = 0;

                    rx[i][3] = 0;
                    ry[i][3] = 0;

                    rx[i][4] = 0;
                    ry[i][4] = 0;

                    rx[i][5] = 0;
                    ry[i][5] = 0;
                }

                try (FileWriter outFile = new FileWriter("mission_out.txt")) {
                    while (venus.getPosition().distanceTo(spaceship.getPosition()) > venus.getRadius() + spaceship.getRadius() + 1200 && iter < 1000000) {
                        if (iter % 10 == 0) {
                            outFile.write("4\n\n");
                            outFile.write(String.format(Locale.ROOT, "%d, %.16f, %.16f, %.16f, %.16f, %.16f\n",
                                    sun.getId(), sun.getPosition().getX(), sun.getPosition().getY(), sun.getVx(), sun.getVy(), sun.getRadius()));
                        }

                        for (int i = 1; i < celestialBodies.size(); i++) {
                            CelestialBody body = celestialBodies.get(i);
                            gear(rx[i], ry[i], 60 * 60, body, celestialBodies.stream().filter(b -> b != body).collect(Collectors.toList()));
                        }

                        for (int i = 1; i < celestialBodies.size(); i++) {
                            CelestialBody body = celestialBodies.get(i);
                            body.getPosition().update(rx[i][0], ry[i][0]);
                            body.updateVelocity(rx[i][1], ry[i][1]);
                            if (iter % 10 == 0)
                                outFile.write(String.format(Locale.ROOT, "%d, %.16f, %.16f, %.16f, %.16f, %.16f\n",
                                        body.getId(), body.getPosition().getX(), body.getPosition().getY(), body.getVx(), body.getVy(), body.getRadius()));
                        }

                        outFile.flush();
                        iter++;
                    }

//                    outFile.write("\n"); // TODO: Ver como separamos condiciones iniciales

                    if (iter < minIter)
                        minIter = iter;
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

    private static void gear(double[] rx, double[] ry, double step, CelestialBody body, List<CelestialBody> celestialBodies) {
        // Predecir
        IntegrationAlgorithms.gearPredR(rx, step);
        IntegrationAlgorithms.gearPredR(ry, step);

        Point oldPosition = new Point(body.getPosition().getX(), body.getPosition().getY());
        double oldVx = body.getVx();
        double oldVy = body.getVy();

        body.getPosition().update(rx[0], ry[0]);
        body.updateVelocity(rx[1], ry[1]);

        double[] predF = body.totalGravitationalForces(celestialBodies);

        body.getPosition().update(oldPosition.getX(), oldPosition.getY());
        body.updateVelocity(oldVx, oldVy);

        // Evaluar
        double currAx = predF[0] / body.getMass();
        double currAy = predF[1] / body.getMass();

        double deltaAx = currAx - rx[2];
        double deltaAy = currAy - ry[2];
        double deltaR2x = deltaAx * Math.pow(step, 2) / 2;
        double deltaR2y = deltaAy * Math.pow(step, 2) / 2;

        // Corregir
        IntegrationAlgorithms.gearCorrectR(ALPHAS, rx, deltaR2x, step);
        IntegrationAlgorithms.gearCorrectR(ALPHAS, ry, deltaR2y, step);
    }
}
