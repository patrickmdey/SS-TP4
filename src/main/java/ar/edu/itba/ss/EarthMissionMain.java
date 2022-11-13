package main.java.ar.edu.itba.ss;

import main.java.ar.edu.itba.ss.models.CelestialBody;
import main.java.ar.edu.itba.ss.models.Point;
import main.java.ar.edu.itba.ss.utils.MissionUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.time.Month;
import java.util.*;

public class EarthMissionMain {
    public static final double[] ALPHAS = new double[]{3.0 / 20, 251.0 / 360, 1, 11.0 / 18, 1.0 / 6, 1.0 / 60};

    public static final int STEP = 300;

    public static final int DATES_TO_TRY = 365 * 2;

    public static boolean hasToAppend = false;

    public static void main(String[] args) {

        CelestialBody sun, earth, venus;
        long minIter = Integer.MAX_VALUE;
        sun = new CelestialBody(0, "Sun", new Point(0, 0), 0, 0, 695_700, 1_988_500 * Math.pow(10, 24), 0);

        double earthX = 0, earthY = 0, earthVx = 0, earthVy = 0;
        double venusX = 0, venusY = 0, venusVx = 0, venusVy = 0;

        try (Scanner backScanner = new Scanner(new File("src/main/java/ar/edu/itba/ss/files/venus-earth.csv"))) {
            backScanner.nextLine();
            //Read line
            while (backScanner.hasNextLine()) {
                String backLine = backScanner.nextLine();
                //Scan the line for tokens
                try (Scanner backRowScanner = new Scanner(backLine)) {
                    backRowScanner.useDelimiter(",");
                    String backPart = backRowScanner.next();
                    if (backPart.equals("Earth")) {
                        earthX = Double.parseDouble(backRowScanner.next());
                        earthY = Double.parseDouble(backRowScanner.next());
                        earthVx = Double.parseDouble(backRowScanner.next());
                        earthVy = Double.parseDouble(backRowScanner.next());
                    } else if (backPart.equals("Venus")) {
                        venusX = Double.parseDouble(backRowScanner.next());
                        venusY = Double.parseDouble(backRowScanner.next());
                        venusVx = Double.parseDouble(backRowScanner.next());
                        venusVy = Double.parseDouble(backRowScanner.next());
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

                CelestialBody spaceship = MissionUtils.launchSpaceship(venus, 8, 1);

                simulateSpaceship(sun, earth, venus, spaceship, day);
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
        LocalDate date = LocalDate.of(2023, Month.JULY, 15);
        date = date.plusDays(offset);

        double[][] rx = new double[4][6];
        double[][] ry = new double[4][6];

        List<CelestialBody> celestialBodies = new ArrayList<>(Arrays.asList(sun, earth, venus,
                spaceship));

        MissionUtils.initializeRs(rx, ry, celestialBodies);

        try (FileWriter outFile = new FileWriter("./outFiles/e_mission_out.txt", hasToAppend);
             FileWriter distanceFile = new FileWriter("./outFiles/e_distance_out.txt", hasToAppend);
             FileWriter velocityFile = new FileWriter("./outFiles/e_velocity_out.txt", hasToAppend)
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
                    double currDist = Math.max(earth.getPosition().distanceTo(spaceship.getPosition())
                            - earth.getRadius(), 0);
                    if (currDist < minDist) {
                        minDist = currDist;
                        minDay = day;
                    }

                    if (currDist <= 0) {
                        System.out.println("Spaceship arrived to Earth");
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
