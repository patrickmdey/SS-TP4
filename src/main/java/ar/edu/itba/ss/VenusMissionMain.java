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
import java.util.*;
import java.util.stream.Collectors;

public class VenusMissionMain {
    public static final double[] ALPHAS = new double[]{3.0 / 20, 251.0 / 360, 1, 11.0 / 18, 1.0 / 6, 1.0 / 60};

    public static final int STEP = 300;

    public static final double STATION_ORBIT_SPEED = 7.12;

    public static final double STATION_ORBIT_HEIGHT = 1500;

    public static final int DATES_TO_TRY = 365;

    public static boolean hasToAppend = false;

    public static void main(String[] args) {

        CelestialBody sun, earth, venus;
        long minIter = Integer.MAX_VALUE;
        sun = new CelestialBody(0, "Sun", new Point(0, 0), 0, 0, 695_700, 1_988_500 * Math.pow(10, 24), 0);

        double takeOffSpeed = 8;

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

                for (int day = 0; day < DATES_TO_TRY; day++) {
                    earth = new CelestialBody(1, "Earth", new Point(earthX, earthY),
                            earthVx, earthVy, 6_371.01, 5.97219 * Math.pow(10, 24),
                            29.79);
                    venus = new CelestialBody(2, "Venus", new Point(venusX, venusY),
                            venusVx, venusVy, 6_051.84, 48.685 * Math.pow(10, 23),
                            35.021);

                    for (int offset = 0; offset < day; offset++)
                        MissionUtils.simulateDay(Arrays.asList(sun, earth, venus));

                    CelestialBody spaceship = MissionUtils.launchSpaceship(earth, 8, -1);

                    simulateSpaceship(sun, earth, venus, spaceship, day);
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            System.exit(1);
        }

        System.out.println(minIter);
    }

    /**
     * Simulates the spaceship orbiting the sun
     */
    public static void simulateSpaceship(CelestialBody sun, CelestialBody earth, CelestialBody venus,
                                         CelestialBody spaceship, int offset) {
        LocalDate date = LocalDate.of(2022, Month.SEPTEMBER, 23);
        date = date.plusDays(offset);

        double[][] rx = new double[4][6];
        double[][] ry = new double[4][6];

        List<CelestialBody> celestialBodies = new ArrayList<>(Arrays.asList(sun, earth, venus,
                spaceship));

        MissionUtils.initializeRs(rx, ry, celestialBodies);

        try (FileWriter outFile = new FileWriter("./outFiles/mission_out.txt", hasToAppend);
             FileWriter distanceFile = new FileWriter("./outFiles/distance_out.txt", hasToAppend);
             FileWriter velocityFile = new FileWriter("./outFiles/velocity_out.txt", hasToAppend)
        ) {
            hasToAppend = true;
            outFile.write(date + "\n");
            double minDist = Double.MAX_VALUE;
            int minDay = Integer.MAX_VALUE;
            boolean crashed = false;

            for (int day = 0; day < DATES_TO_TRY && !crashed; day++) {
                int elapsed = 0;
                while (elapsed < 24 * 60 * 60) {
                    elapsed += STEP;
                    double currDist = Math.max(venus.getPosition().distanceTo(spaceship.getPosition())
                            - venus.getRadius(), 0);
                    if (currDist < minDist) {
                        minDist = currDist;
                        minDay = day;
                    }

                    if (currDist <= 0) {
                        System.out.println("Spaceship arrived to venus");
                        crashed = true;
                        break;
                    }

                    MissionUtils.twoDimensionalGear(celestialBodies, rx, ry);

                    if (elapsed % (STEP * 100) == 0) {
                        outFile.write("4\n\n");
                        for (CelestialBody body : celestialBodies) {
                            outFile.write(String.format(Locale.ROOT, "%d, %.16f, %.16f, %.16f, %.16f, %.16f, %.16f\n",
                                    body.getId(), body.getPosition().getX(), body.getPosition().getY(), body.getVx(), body.getVy(), body.getRadius(), body.getMass()));

                        }
                        velocityFile.write(date + "\n");
                        velocityFile.write(String.format(Locale.ROOT,
                                "%.16f, %.16f\n", spaceship.getVx(), spaceship.getVy()));
                    }
                    outFile.flush();
                }
            }
            distanceFile.write(String.format(Locale.ROOT, "%s,%s,%f\n", date, date.plusDays(minDay), minDist));
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }
}