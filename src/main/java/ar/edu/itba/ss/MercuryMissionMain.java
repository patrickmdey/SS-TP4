package main.java.ar.edu.itba.ss;

import main.java.ar.edu.itba.ss.VenusMission.models.CelestialBody;
import main.java.ar.edu.itba.ss.VenusMission.models.Point;
import main.java.ar.edu.itba.ss.utils.IntegrationAlgorithms;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.time.Month;
import java.util.*;
import java.util.stream.Collectors;

public class MercuryMissionMain {
    public static final double[] ALPHAS = new double[]{3.0 / 20, 251.0 / 360, 1, 11.0 / 18, 1.0 / 6, 1.0 / 60};

    public static final int STEP = 300; // TODO: juli dice

    public static final double STATION_ORBIT_SPEED = 7.12;

    public static final double STATION_ORBIT_HEIGHT = 1500;

    public static final int DATES_TO_TRY = 365;

    public static boolean hasToAppend = false;

    public static void main(String[] args) {

        CelestialBody sun, earth, venus, mercury;
        long minIter = Integer.MAX_VALUE;//695700
        sun = new CelestialBody(0, "Sun", new Point(0, 0), 0, 0, 695_700, 1_988_500 * Math.pow(10, 24), 0);

        double takeOffSpeed = 8;
        double earthOrbitSpeed = 7.12;

        double earthX = 0, earthY = 0, earthVx = 0, earthVy = 0;
        double venusX = 0, venusY = 0, venusVx = 0, venusVy = 0;
        double mercuryX = 0, mercuryY = 0, mercuryVx = 0, mercuryVy = 0;

        try (Scanner earthScanner = new Scanner(new File("src/main/java/ar/edu/itba/ss/files/earth-horizons.csv"));
             Scanner venusScanner = new Scanner(new File("src/main/java/ar/edu/itba/ss/files/venus-horizons.csv"));
             Scanner mercuryScanner = new Scanner(new File("src/main/java/ar/edu/itba/ss/files/mercury-horizons.csv"))) {
            earthScanner.nextLine();
            venusScanner.nextLine();
            mercuryScanner.nextLine();
            //Read line
            while (earthScanner.hasNextLine() && venusScanner.hasNextLine() && mercuryScanner.hasNextLine()) {
                String earthLine = earthScanner.nextLine();
                String venusLine = venusScanner.nextLine();
                String mercuryLine = mercuryScanner.nextLine();
                //Scan the line for tokens
                try (Scanner earthRowScanner = new Scanner(earthLine);
                     Scanner venusRowScanner = new Scanner(venusLine);
                     Scanner mercuryRowScanner = new Scanner(mercuryLine);) {
                    earthRowScanner.useDelimiter(",");
                    venusRowScanner.useDelimiter(",");
                    mercuryRowScanner.useDelimiter(",");
                    for (int i = 0; earthRowScanner.hasNext() && venusRowScanner.hasNext() && mercuryRowScanner.hasNext(); i++) {
                        String earthPart = earthRowScanner.next();
                        String venusPart = venusRowScanner.next();
                        String mercuryPart = mercuryRowScanner.next();
                        switch (i) {
                            case 2: {
                                earthX = Double.parseDouble(earthPart);
                                venusX = Double.parseDouble(venusPart);
                                mercuryX = Double.parseDouble(mercuryPart);
                                break;
                            }
                            case 3: {
                                earthY = Double.parseDouble(earthPart);
                                venusY = Double.parseDouble(venusPart);
                                mercuryY = Double.parseDouble(mercuryPart);
                                break;
                            }
                            case 5: {
                                earthVx = Double.parseDouble(earthPart);
                                venusVx = Double.parseDouble(venusPart);
                                mercuryVx = Double.parseDouble(mercuryPart);
                                break;
                            }
                            case 6: {
                                earthVy = Double.parseDouble(earthPart);
                                venusVy = Double.parseDouble(venusPart);
                                mercuryVy = Double.parseDouble(mercuryPart);
                                break;
                            }
                        }
                    }
                }

                for (int day = 0; day < DATES_TO_TRY; day++) {
                    //    for (int day = 228; day < 229; day++) {
                    // TODO: capaz se podría guardar en un archivo las ultimas posiciones o algo
                    earth = new CelestialBody(1, "Earth", new Point(earthX, earthY),
                            earthVx, earthVy, 6_371.01, 5.97219 * Math.pow(10, 24),
                            29.79);
                    venus = new CelestialBody(2, "Venus", new Point(venusX, venusY),
                            venusVx, venusVy, 6_051.84, 48.685 * Math.pow(10, 23),
                            35.021);

                    mercury = new CelestialBody(3, "Mercury", new Point(mercuryX, mercuryY),
                            mercuryVx, mercuryVy, 2_440, 3.302 * Math.pow(10, 23),
                            47.362);

                    for (int offset = 0; offset < day; offset++)
                        simulateDay(Arrays.asList(sun, earth, venus));

                    CelestialBody spaceship = launchSpaceship(earth);

                    simulateSpaceship(sun, earth, venus, mercury, spaceship, day);
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            System.exit(1);
        }

        System.out.println(minIter);
    }

    /**
     * Recreates the spaceship launch
     */
    private static CelestialBody launchSpaceship(CelestialBody earth) {
        double spaceshipOrbitRadius = earth.getRadius() + STATION_ORBIT_HEIGHT;

        double earthDistanceToSun = earth.getPosition().distanceTo(new Point(0, 0));

        // esta del lado izquierdo
        double spaceshipDistanceToSun = earthDistanceToSun - spaceshipOrbitRadius;

        double theta = Math.atan2(earth.getPosition().getY(), earth.getPosition().getX());

        // Pasamos de normal a cartesiano
        double x = Math.cos(theta) * spaceshipDistanceToSun;
        double y = Math.sin(theta) * spaceshipDistanceToSun;

        // Pasamos de tangencial a cartesiano
        double vOrb = Math.sqrt(Math.pow(earth.getVx(), 2) + Math.pow(earth.getVy(), 2));
        double vOrbTot = vOrb - 8 - STATION_ORBIT_SPEED; // TODO hardcodeado el 8, pero despues es variable

        double vx = -Math.sin(theta) * vOrbTot;
        double vy = Math.cos(theta) * vOrbTot;

        return new CelestialBody(4, "Spaceship", new Point(x, y), vx, vy, 0,
                2 * Math.pow(10, 5), 0);
    }

    /**
     * Simulates the planets orbiting the sun
     */
    public static void simulateDay(List<CelestialBody> celestialBodies) {
        double elapsed = 0;
        double[][] rx = new double[3][6];
        double[][] ry = new double[3][6];
        initializeRs(rx, ry, celestialBodies);

        while (Double.compare(elapsed, 24 * 60 * 60) < 0) {
            elapsed += STEP;
            twoDimensionalGear(celestialBodies, rx, ry);
        }
    }

    /**
     * Simulates the spaceship orbiting the sun
     */
    public static void simulateSpaceship(CelestialBody sun, CelestialBody earth, CelestialBody venus, CelestialBody mercury,
                                         CelestialBody spaceship, int offset) {
        LocalDate date = LocalDate.of(2022, Month.SEPTEMBER, 23);
        date = date.plusDays(offset);
        long minIter = Integer.MAX_VALUE;//695700
        long iter = 0;

        double[][] rx = new double[5][6];
        double[][] ry = new double[5][6];

        List<CelestialBody> celestialBodies = new ArrayList<>(Arrays.asList(sun, earth, venus, mercury,
                spaceship));

        initializeRs(rx, ry, celestialBodies);

        try (FileWriter outFile = new FileWriter("mission_out.txt", hasToAppend);
             FileWriter distanceFile = new FileWriter("distance_out.txt", hasToAppend)) {
            hasToAppend = true;
            outFile.write(date + "\n");
            double minDist = Double.MAX_VALUE;
            int minDay = Integer.MAX_VALUE;
            boolean crashed = false;

            //while (venus.getPosition().distanceTo(spaceship.getPosition()) > venus.getRadius() + spaceship.getRadius() + 1200 && iter < 1000000) {
            for (int day = 0; day < DATES_TO_TRY && !crashed; day++) {
                double elapsed = 0;
                while (Double.compare(elapsed, 24 * 60 * 60) < 0) {
                    elapsed += STEP;
                    double currDist = venus.getPosition().distanceTo(spaceship.getPosition());
                    if (currDist < minDist) {
                        minDist = currDist;
                        minDay = day;
                    }

                    if (currDist <= venus.getRadius() + spaceship.getRadius()) {
                        System.out.println("Spaceship arrived to venus");
                        crashed = true;
                        break; // TODO ver si hacemos algo mas y si funca
                    }

                    twoDimensionalGear(celestialBodies, rx, ry);

                    if (elapsed % (STEP * 100) == 0) {
                        outFile.write(String.format("%d\n\n", celestialBodies.size()));
                        for (CelestialBody body : celestialBodies) {
                            outFile.write(String.format(Locale.ROOT, "%d, %.16f, %.16f, %.16f, " + "%.16f, %.16f\n",
                                    body.getId(), body.getPosition().getX(), body.getPosition().getY(), body.getVx(), body.getVy(), body.getRadius()));

                        }
                    }
                    outFile.flush();
                    iter++;
                }
            }
            distanceFile.write(String.format("%s %s %f\n", date, date.plusDays(minDay), minDist));
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
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

    public static void twoDimensionalGear(List<CelestialBody> celestialBodies, double[][] rx, double[][] ry){
        for (int i = 1; i < celestialBodies.size(); i++) {
            CelestialBody body = celestialBodies.get(i);
            IntegrationAlgorithms.gearPredR(rx[i], STEP);
            IntegrationAlgorithms.gearPredR(ry[i], STEP);

            body.getPosition().update(rx[i][0], ry[i][0]); //Son las predicciones
            body.updateVelocity(rx[i][1], ry[i][1]);
        }

        double[] deltaR2x = new double[5];
        double[] deltaR2y = new double[5];

        // Evaluar
        for (int i = 1; i < celestialBodies.size(); i++) {
            CelestialBody body = celestialBodies.get(i);
            double[] predF = body.totalGravitationalForces(celestialBodies.stream().filter(b -> b != body).collect(Collectors.toList()));

            double currAx = predF[0] / body.getMass();
            double currAy = predF[1] / body.getMass();

            double deltaAx = currAx - rx[i][2];
            double deltaAy = currAy - ry[i][2];
            deltaR2x[i] = deltaAx * Math.pow(STEP, 2) / 2;
            deltaR2y[i] = deltaAy * Math.pow(STEP, 2) / 2;
        }

        // Corregir
        for (int i = 1; i < celestialBodies.size(); i++) {
            IntegrationAlgorithms.gearCorrectR(ALPHAS, rx[i], deltaR2x[i], STEP);
            IntegrationAlgorithms.gearCorrectR(ALPHAS, ry[i], deltaR2y[i], STEP);

            CelestialBody body = celestialBodies.get(i);
            body.getPosition().update(rx[i][0], ry[i][0]);
            body.updateVelocity(rx[i][1], ry[i][1]);
        }
    }
}