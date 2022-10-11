package main.java.ar.edu.itba.ss;

import main.java.ar.edu.itba.ss.VenusMission.models.CelestialBody;
import main.java.ar.edu.itba.ss.VenusMission.models.Point;
import main.java.ar.edu.itba.ss.utils.IntegrationAlgorithms;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

public class MarsSweepDayAnalysis {
    public static final double[] ALPHAS = new double[]{3.0 / 20, 251.0 / 360, 1, 11.0 / 18, 1.0 / 6, 1.0 / 60};

    public static final int STEP = 300;

    public static final double STATION_ORBIT_SPEED = 7.12;

    public static final double STATION_ORBIT_HEIGHT = 1500;

    public static final int DATES_TO_TRY = 3;

    public static boolean hasToAppend = false;

    public static void main(String[] args) {
        CelestialBody sun, earth, mars;
        long minIter = Integer.MAX_VALUE;
        sun = new CelestialBody(0, "Sun", new Point(0, 0), 0, 0,
                695_700, 1_988_500 * Math.pow(10, 24), 0);

        double earthX = 0, earthY = 0, earthVx = 0, earthVy = 0;
        double marsX = 0, marsY = 0, marsVx = 0, marsVy = 0;

        try (Scanner earthScanner = new Scanner(new File("src/main/java/ar/edu/itba/ss/files/earth-horizons.csv"));
             Scanner marsScanner = new Scanner(new File("src/main/java/ar/edu/itba/ss/files/mars-horizons.csv"))) {
            earthScanner.nextLine();
            marsScanner.nextLine();
            //Read line
            while (earthScanner.hasNextLine() && marsScanner.hasNextLine()) {
                String earthLine = earthScanner.nextLine();
                String marsLine = marsScanner.nextLine();
                //Scan the line for tokens
                try (Scanner earthRowScanner = new Scanner(earthLine); Scanner marsRowScanner = new Scanner(marsLine)) {
                    earthRowScanner.useDelimiter(",");
                    marsRowScanner.useDelimiter(",");
                    for (int i = 0; earthRowScanner.hasNext() && marsRowScanner.hasNext(); i++) {
                        String earthPart = earthRowScanner.next();
                        String marsPart = marsRowScanner.next();
                        switch (i) {
                            case 2: {
                                earthX = Double.parseDouble(earthPart);
                                marsX = Double.parseDouble(marsPart);
                                break;
                            }
                            case 3: {
                                earthY = Double.parseDouble(earthPart);
                                marsY = Double.parseDouble(marsPart);
                                break;
                            }
                            case 5: {
                                earthVx = Double.parseDouble(earthPart);
                                marsVx = Double.parseDouble(marsPart);
                                break;
                            }
                            case 6: {
                                earthVy = Double.parseDouble(earthPart);
                                marsVy = Double.parseDouble(marsPart);
                                break;
                            }
                        }
                    }
                }

                LocalDate startDate = LocalDate.of(2022, Month.SEPTEMBER, 9);
                LocalDate optimalDate = LocalDate.of(2022, Month.SEPTEMBER, 8);

                long days = startDate.until(optimalDate, ChronoUnit.DAYS);

                for (int day = 0; day < DATES_TO_TRY; day++) { // check 3 days
                    int dayOffset = 0;
                    while (Double.compare(dayOffset, 60 * 60 * 24) < 0) {
                        // simulate every 10 minutes
                        earth = new CelestialBody(1, "Earth", new Point(earthX, earthY),
                                earthVx, earthVy, 6_371.01, 5.97219 * Math.pow(10, 24),
                                29.79);
                        mars = new CelestialBody(2, "mars", new Point(marsX, marsY),
                                marsVx, marsVy, 6_051.84, 48.685 * Math.pow(10, 23),
                                35.021);

                        simulateDay(Arrays.asList(sun, earth, mars), day + days, dayOffset);

                        CelestialBody spaceship = launchSpaceship(earth);

                        simulateSpaceship(sun, earth, mars, spaceship, day, dayOffset);

                        dayOffset += 10 * 60;
                    }
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    /**
     * Recreates the spaceship launch
     */
    private static CelestialBody launchSpaceship(CelestialBody earth) {
        double spaceshipOrbitRadius = earth.getRadius() + STATION_ORBIT_HEIGHT;

        double earthDistanceToSun = earth.getPosition().distanceTo(new Point(0, 0));

        // esta del lado izquierdo
        double spaceshipDistanceToSun = earthDistanceToSun + spaceshipOrbitRadius;

        double theta = Math.atan2(earth.getPosition().getY(), earth.getPosition().getX());

        // Pasamos de normal a cartesiano
        double x = Math.cos(theta) * spaceshipDistanceToSun;
        double y = Math.sin(theta) * spaceshipDistanceToSun;

        // Pasamos de tangencial a cartesiano
        double vOrb = Math.sqrt(Math.pow(earth.getVx(), 2) + Math.pow(earth.getVy(), 2));
        double vOrbTot = vOrb + 8 + STATION_ORBIT_SPEED; // TODO hardcodeado el 8, pero despues es variable

        double vx = -Math.sin(theta) * vOrbTot;
        double vy = Math.cos(theta) * vOrbTot;

        return new CelestialBody(3, "Spaceship", new Point(x, y), vx, vy, 0,
                2 * Math.pow(10, 5), 0);
    }

    /**
     * Simulates the planets orbiting the sun
     */
    public static void simulateDay(List<CelestialBody> celestialBodies,
                                   double daysToSim, int dayOffset) {
        double elapsed = 0;
        double[][] rx = new double[3][6];
        double[][] ry = new double[3][6];
        initializeRs(rx, ry, celestialBodies);
        while (Double.compare(elapsed, 24 * 60 * 60 * daysToSim + dayOffset) < 0) {
            elapsed += STEP;
            twoDimensionalGear(celestialBodies, rx, ry);
        }

    }

    /**
     * Simulates the spaceship orbiting the sun
     */
    public static void simulateSpaceship(CelestialBody sun, CelestialBody earth, CelestialBody mars,
                                         CelestialBody spaceship, int offset, int minutesOffset) {
        LocalDate date = LocalDate.of(2023, Month.MAY, 8);
        date = date.plusDays(offset);

        int h = (minutesOffset / 3600);

        int m = (minutesOffset - (3600 * h)) / 60;

        LocalDateTime dateToPrint = date.atTime(h, m);

        double[][] rx = new double[4][6];
        double[][] ry = new double[4][6];

        List<CelestialBody> celestialBodies = new ArrayList<>(Arrays.asList(sun, earth, mars,
                spaceship));

        initializeRs(rx, ry, celestialBodies);

        try (FileWriter outFile = new FileWriter("sweep_mission_out.txt", hasToAppend);
             FileWriter distanceFile = new FileWriter("sweep_distance_out.txt", hasToAppend);
             FileWriter velocityFile = new FileWriter("sweep_velocity_out.txt", hasToAppend)) {
            hasToAppend = true;
            outFile.write(dateToPrint + "\n");
            double minDist = Double.MAX_VALUE;
            int minDay = Integer.MAX_VALUE;
            boolean crashed = false;
            int minElapsed = Integer.MAX_VALUE;
            for (int day = 0; day < 365 && !crashed; day++) { // todo hardcodeado
                int elapsed = 0;
                while (elapsed < 24 * 60 * 60) {

                    double currDist = Math.max(mars.getPosition().distanceTo(spaceship.getPosition())
                            - mars.getRadius(), 0);
                    if (currDist < minDist) {
                        minDist = currDist;
                        minDay = day;
                        minElapsed = elapsed;
                    }

                    if (currDist <= 0) {
                        System.out.println("Spaceship landed on mars");
                        crashed = true;
                        System.out.println("Relative velocity was: " +
                                Math.sqrt(Math.pow(spaceship.getVx() - mars.getVx(), 2) +
                                        Math.pow(spaceship.getVy() - mars.getVy(), 2)));
                        break;
                    }

                    twoDimensionalGear(celestialBodies, rx, ry);

                    if (elapsed % (STEP * 100) == 0) {
                        outFile.write("4\n\n");
                        for (CelestialBody body : celestialBodies) {
                            outFile.write(String.format(Locale.ROOT, "%d, %.16f, %.16f, %.16f, " + "%.16f, %.16f\n",
                                    body.getId(), body.getPosition().getX(), body.getPosition().getY(), body.getVx(), body.getVy(), body.getRadius()));
                        }
                        velocityFile.write(String.format(Locale.ROOT,
                                "%.16f, %.16f\n", spaceship.getVx(), spaceship.getVy()));
                    }
                    elapsed += STEP; //TODO: estaba ni bien arrancaba el for. Checkear
                    outFile.flush();
                }
            }
            distanceFile.write(String.format(Locale.ROOT, "%s,%s,%f\n", dateToPrint,
                    dateToPrint.plusDays(minDay).plus(minElapsed, ChronoUnit.SECONDS), minDist));
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

    public static void twoDimensionalGear(List<CelestialBody> celestialBodies, double[][] rx, double[][] ry) {
        for (int i = 1; i < celestialBodies.size(); i++) {
            CelestialBody body = celestialBodies.get(i);
            IntegrationAlgorithms.gearPredR(rx[i], STEP);
            IntegrationAlgorithms.gearPredR(ry[i], STEP);

            body.getPosition().update(rx[i][0], ry[i][0]); //Son las predicciones
            body.updateVelocity(rx[i][1], ry[i][1]);
        }

        double[] deltaR2x = new double[4];
        double[] deltaR2y = new double[4];

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