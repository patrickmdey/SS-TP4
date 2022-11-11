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

public class MarsMissionMain {
    public static final double[] ALPHAS = new double[]{3.0 / 20, 251.0 / 360, 1, 11.0 / 18, 1.0 / 6, 1.0 / 60};

    public static final int STEP = 300;

    public static final double STATION_ORBIT_SPEED = 7.12;

    public static final double STATION_ORBIT_HEIGHT = 1500;

    public static final int DATES_TO_TRY = 2 * 686;

    public static boolean hasToAppend = false;

    public static void main(String[] args) {

        CelestialBody sun, earth, mars;
        long minIter = Integer.MAX_VALUE;//695700
        sun = new CelestialBody(0, "Sun", new Point(0, 0), 0, 0, 695_700, 1_988_500 * Math.pow(10, 24), 0);

        double takeOffSpeed = 8;

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
                try (Scanner earthRowScanner = new Scanner(earthLine);
                     Scanner marsRowScanner = new Scanner(marsLine);) {
                    earthRowScanner.useDelimiter(",");
                    marsRowScanner.useDelimiter(",");
                    for (int i = 0; earthRowScanner.hasNext()  && marsRowScanner.hasNext(); i++) {
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

                for (int day = 0; day < DATES_TO_TRY; day++) {
                    //    for (int day = 228; day < 229; day++) {
                    // TODO: capaz se podría guardar en un archivo las ultimas posiciones o algo
                    earth = new CelestialBody(1, "Earth", new Point(earthX, earthY),
                            earthVx, earthVy, 6_371.01, 5.97219 * Math.pow(10, 24),
                            29.79);

                    mars = new CelestialBody(2, "Mars", new Point(marsX, marsY),
                            marsVx, marsVy, 3_389.92, 6.4171 * Math.pow(10, 23),
                            24.13);

                    for (int offset = 0; offset < day; offset++)
                        MissionUtils.simulateDay(Arrays.asList(sun, earth, mars));

                    CelestialBody spaceship = MissionUtils.launchSpaceship(earth, 8, 1);

                    simulateSpaceship(sun, earth, mars, spaceship, day);
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
    public static void simulateSpaceship(CelestialBody sun, CelestialBody earth, CelestialBody mars,
                                         CelestialBody spaceship, int offset) {
        LocalDate date = LocalDate.of(2022, Month.SEPTEMBER, 23);
        date = date.plusDays(offset);

        double[][] rx = new double[4][6];
        double[][] ry = new double[4][6];

        List<CelestialBody> celestialBodies = new ArrayList<>(Arrays.asList(sun, earth, mars,
                spaceship));

        MissionUtils.initializeRs(rx, ry, celestialBodies);

        try (FileWriter outFile = new FileWriter("./outFiles/mission_out.txt", hasToAppend);
             FileWriter distanceFile = new FileWriter("./outFiles/distance_out.txt", hasToAppend)) {
            hasToAppend = true;
            outFile.write(date + "\n");
            double minDist = Double.MAX_VALUE;
            int minDay = Integer.MAX_VALUE;
            boolean crashed = false;

            for (int day = 0; day < DATES_TO_TRY && !crashed; day++) {
                double elapsed = 0;
                while (Double.compare(elapsed, 24 * 60 * 60) < 0) {
                    elapsed += STEP;
                    double currDist = mars.getPosition().distanceTo(spaceship.getPosition());
                    if (currDist < minDist) {
                        minDist = currDist;
                        minDay = day;
                    }

                    if (currDist <= mars.getRadius() + spaceship.getRadius()) {
                        System.out.println("Spaceship arrived to mars");
                        crashed = true;
                        break; // TODO ver si hacemos algo mas y si funca
                    }

                    MissionUtils.twoDimensionalGear(celestialBodies, rx, ry);

                    if (elapsed % (STEP * 100) == 0) {
                        outFile.write(String.format("%d\n\n", celestialBodies.size()));
                        for (CelestialBody body : celestialBodies) {
                            outFile.write(String.format(Locale.ROOT, "%d, %.16f, %.16f, %.16f, %.16f, %.16f, %.16f\n",
                                    body.getId(), body.getPosition().getX(), body.getPosition().getY(), body.getVx(), body.getVy(), body.getRadius(), body.getMass()));

                        }
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