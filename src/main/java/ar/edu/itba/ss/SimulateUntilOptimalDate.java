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

public class SimulateUntilOptimalDate {
    public static final int OPTIMAL_DAY = 288;

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
                earth = new CelestialBody(1, "Earth", new Point(earthX, earthY),
                        earthVx, earthVy, 6_371.01, 5.97219 * Math.pow(10, 24),
                        29.79);
                venus = new CelestialBody(2, "Venus", new Point(venusX, venusY),
                        venusVx, venusVy, 6_051.84, 48.685 * Math.pow(10, 23),
                        35.021);

                for (int offset = 0; offset < OPTIMAL_DAY+1; offset++)
                    MissionUtils.simulateDay(Arrays.asList(sun, earth, venus));

                try (FileWriter writer = new FileWriter("src/main/java/ar/edu/itba/ss/files/venus-earth.csv")) {
                    writer.write("name,x,y,vx,vy\n");
                    writer.write("Earth," + earth.getPosition().getX() + "," + earth.getPosition().getY() + "," + earth.getVx() + "," + earth.getVy() + "\n");
                    writer.write("Venus," + venus.getPosition().getX() + "," + venus.getPosition().getY() + "," + venus.getVx() + "," + venus.getVy() + "\n");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            System.exit(1);
        }
        System.out.println(minIter);
    }
}
