package main.java.ar.edu.itba.ss;

import main.java.ar.edu.itba.ss.models.CelestialBody;
import main.java.ar.edu.itba.ss.models.Point;
import main.java.ar.edu.itba.ss.utils.MissionUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.temporal.ChronoUnit;
import java.util.*;

public class EarthSweepDayAnalysis {
    public static final int STEP = 300;
    public static final int DATES_TO_TRY = 3;

    public static boolean hasToAppend = false;

    public static void main(String[] args) throws FileNotFoundException {
        CelestialBody sun, earth, venus;
        sun = new CelestialBody(0, "Sun", new Point(0, 0), 0, 0,
                695_700, 1_988_500 * Math.pow(10, 24), 0);

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


            LocalDate startDate = LocalDate.of(2022, Month.JULY, 15);
            LocalDate optimalDate = LocalDate.of(2023, Month.JULY, 14);

            for (int day = 0; day < DATES_TO_TRY; day++) { // check 3 days
                int dayOffset = 0;
                while (Double.compare(dayOffset, 60 * 60 * 24) < 0) {
                    // simulate every 10 minutes
                    earth = new CelestialBody(1, "Earth", new Point(earthX, earthY),
                            earthVx, earthVy, 6_371.01, 5.97219 * Math.pow(10, 24),
                            29.79);
                    venus = new CelestialBody(2, "Venus", new Point(venusX, venusY),
                            venusVx, venusVy, 6_051.84, 48.685 * Math.pow(10, 23),
                            35.021);

                    simulateDay(Arrays.asList(sun, earth, venus), day, dayOffset);

                    CelestialBody spaceship = MissionUtils.launchSpaceship(earth, 8, -1);

                    simulateSpaceship(sun, earth, venus, spaceship, day, dayOffset);

                    dayOffset += 10 * 60;
                }
            }

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
    public static void simulateSpaceship(CelestialBody sun, CelestialBody earth, CelestialBody venus,
                                         CelestialBody spaceship, int offset, int minutesOffset) {
        LocalDate date = LocalDate.of(2023, Month.JULY, 14);
        date = date.plusDays(offset);

        int h = (minutesOffset / 3600);

        int m = (minutesOffset - (3600 * h)) / 60;

        LocalDateTime dateToPrint = date.atTime(h, m);

        double[][] rx = new double[4][6];
        double[][] ry = new double[4][6];

        List<CelestialBody> celestialBodies = new ArrayList<>(Arrays.asList(sun, earth, venus,
                spaceship));

        MissionUtils.initializeRs(rx, ry, celestialBodies);

        try (FileWriter outFile = new FileWriter("./outFiles/e_sweep_mission_out.txt", hasToAppend);
             FileWriter distanceFile = new FileWriter("./outFiles/e_sweep_distance_out.txt", hasToAppend);
             FileWriter velocityFile = new FileWriter("./outFiles/e_sweep_velocity_out.txt", hasToAppend)) {
            hasToAppend = true;
            outFile.write(dateToPrint + "\n");
            double minDist = Double.MAX_VALUE;
            int minDay = Integer.MAX_VALUE;
            boolean crashed = false;
            int minElapsed = Integer.MAX_VALUE;
            for (int day = 0; day < 365 && !crashed; day++) { // todo hardcodeado
                int elapsed = 0;
                while (elapsed < 24 * 60 * 60) {

                    double currDist = Math.max(earth.getPosition().distanceTo(spaceship.getPosition())
                            - earth.getRadius(), 0);
                    if (currDist < minDist) {
                        minDist = currDist;
                        minDay = day;
                        minElapsed = elapsed;
                    }

                    if (currDist <= 0) {
                        System.out.println("Spaceship landed on venus");
                        crashed = true;
                        System.out.println("Relative velocity was: " +
                                Math.sqrt(Math.pow(spaceship.getVx() - earth.getVx(), 2) +
                                        Math.pow(spaceship.getVy() - earth.getVy(), 2)));
                        break;
                    }

                    MissionUtils.twoDimensionalGear(celestialBodies, rx, ry);

                    if (elapsed % (STEP * 100) == 0) {
                    outFile.write("4\n\n");
                    for (CelestialBody body : celestialBodies) {
                        outFile.write(String.format(Locale.ROOT, "%d, %.16f, %.16f, %.16f, %.16f, %.16f, %.16f\n",
                                body.getId(), body.getPosition().getX(), body.getPosition().getY(), body.getVx(), body.getVy(), body.getRadius(), body.getMass()));
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
}
