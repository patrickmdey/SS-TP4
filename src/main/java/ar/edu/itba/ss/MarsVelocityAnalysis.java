package main.java.ar.edu.itba.ss;

import main.java.ar.edu.itba.ss.models.CelestialBody;
import main.java.ar.edu.itba.ss.models.Point;
import main.java.ar.edu.itba.ss.utils.IntegrationAlgorithms;
import main.java.ar.edu.itba.ss.utils.MissionUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.time.Month;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

public class MarsVelocityAnalysis {
    public static final double[] ALPHAS = new double[]{3.0 / 20, 251.0 / 360, 1, 11.0 / 18, 1.0 / 6, 1.0 / 60};

    public static final int STEP = 300;

    public static final double STATION_ORBIT_SPEED = 7.12;

    public static final double STATION_ORBIT_HEIGHT = 1500;

    public static final int DATES_TO_TRY = 3;

    public static boolean hasToAppend = false;

    public static void main(String[] args) {
        if (args.length != 1) {
            System.out.println("Usage: java -jar VenusVelocityAnalysis.jar <V0>");
            return;
        }

        double v0 = Double.parseDouble(args[0]);
        CelestialBody sun, earth, mars;

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

                LocalDate startDate = LocalDate.of(2022, Month.SEPTEMBER, 23);
                LocalDate optimalDate = LocalDate.of(2024, Month.OCTOBER, 19);

                long days = startDate.until(optimalDate, ChronoUnit.DAYS);
                int optimalHour = 15;
                int optimalMinute = 50;
                int startOffset = optimalHour * 60 * 60 + optimalMinute * 60;
                earth = new CelestialBody(1, "Earth", new Point(earthX, earthY),
                        earthVx, earthVy, 6_371.01, 5.97219 * Math.pow(10, 24),
                        29.79);
                mars = new CelestialBody(2, "Mars", new Point(marsX, marsY),
                        marsVx, marsVy, 3_389.92, 6.4171 * Math.pow(10, 23),
                        24.13);

                simulateDay(Arrays.asList(sun, earth, mars), days, startOffset);

                CelestialBody spaceship = MissionUtils.launchSpaceship(earth, v0, 1);

                simulateSpaceship(sun, earth, mars, spaceship);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    /**
     * Simulates the planets orbiting the sun
     */
    public static void simulateDay(List<CelestialBody> celestialBodies,
                                   double daysToSim, int dayOffset) {
        double elapsed = 0;
        double[][] rx = new double[3][6];
        double[][] ry = new double[3][6];
        MissionUtils.initializeRs(rx, ry, celestialBodies);
        while (Double.compare(elapsed, 24 * 60 * 60 * daysToSim + dayOffset) < 0) {
            elapsed += STEP;
            MissionUtils.twoDimensionalGear(celestialBodies, rx, ry);
        }

    }

    /**
     * Simulates the spaceship orbiting the sun
     */
    public static void simulateSpaceship(CelestialBody sun, CelestialBody earth, CelestialBody mars,
                                         CelestialBody spaceship) {
        double[][] rx = new double[4][6];
        double[][] ry = new double[4][6];

        List<CelestialBody> celestialBodies = new ArrayList<>(Arrays.asList(sun, earth, mars,
                spaceship));

        MissionUtils.initializeRs(rx, ry, celestialBodies);

        try (FileWriter outFile = new FileWriter("./outFiles/v_mission_out.txt", hasToAppend)) {
            hasToAppend = true;
            double minDist = Double.MAX_VALUE;
            boolean crashed = false;
            for (int day = 0; day < 365 && !crashed; day++) { // todo hardcodeado
                int elapsed = 0;
                while (elapsed < 24 * 60 * 60) {
                    double currDist = Math.max(mars.getPosition().distanceTo(spaceship.getPosition())
                            - mars.getRadius(), 0);
                    if (currDist < minDist) {
                        minDist = currDist;
                    }

                    if (currDist <= 0) {
                        System.out.println("Spaceship landed on mars");
                        crashed = true;
                        break;
                    }

                    MissionUtils.twoDimensionalGear(celestialBodies, rx, ry);
                    outFile.write("4\n\n");
                    for (CelestialBody body : celestialBodies) {
                        outFile.write(String.format(Locale.ROOT, "%d, %.16f, %.16f, %.16f, " + "%.16f, %.16f\n",
                                body.getId(), body.getPosition().getX(), body.getPosition().getY(), body.getVx(), body.getVy(), body.getRadius()));
                    }
                    elapsed += STEP; //TODO: estaba ni bien arrancaba el for. Checkear
                    outFile.flush();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

}
