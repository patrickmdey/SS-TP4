package main.java.ar.edu.itba.ss;

import main.java.ar.edu.itba.ss.models.CelestialBody;
import main.java.ar.edu.itba.ss.models.Point;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;


public class Main {
    public static void main(String[] args) {
        CelestialBody sun = null, earth = null, venus = null;
        sun = new CelestialBody("Sun", new Point(0, 0), 0, 0, 6.9551 * Math.pow(10, 5), 1988500 * Math.pow(10, 24), 0);

        long earthX = 0, earthY = 0, earthVx = 0, earthVy = 0;
        long venusX = 0, venusY = 0, venusVx = 0, venusVy = 0;
        try (Scanner earthScanner = new Scanner(new File("/Users/patrick/Desktop/ITBA/SS/SS-TP4/src/main/java/ar/edu/itba/ss/files/earth-horizons.csv")); Scanner venusScanner = new Scanner(new File("/Users/patrick/Desktop/ITBA/SS/SS-TP4/src/main/java/ar/edu/itba/ss/files/venus-horizons.csv"))) {

            earthScanner.nextLine();
            venusScanner.nextLine();
            //Read line
            while (earthScanner.hasNextLine() && venusScanner.hasNextLine()) {
                String line = earthScanner.nextLine();
                String venusLine = earthScanner.nextLine();
                //Scan the line for tokens
                try (Scanner earthRowScanner = new Scanner(line); Scanner venusRowScanner = new Scanner(venusLine)) {
                    earthRowScanner.useDelimiter(",");
                    for (int i = 0; earthRowScanner.hasNext(); i++) {
                        String earthPart = earthRowScanner.next();
                        String venusPart = venusRowScanner.next();

                        switch (i) {
                            case 2: {
                                earthX = Double.valueOf(earthPart).longValue();
                                venusX = Double.valueOf(venusPart).longValue();
                                break;
                            }
                            case 3: {
                                earthY = Double.valueOf(earthPart).longValue();
                                venusY = Double.valueOf(venusPart).longValue();
                                break;
                            }
                            case 4: {
                                earthVx = Double.valueOf(earthPart).longValue();
                                venusVx = Double.valueOf(venusPart).longValue();
                                break;
                            }
                            case 5: {
                                earthVy = Double.valueOf(earthPart).longValue();
                                venusVy = Double.valueOf(venusPart).longValue();
                                break;
                            }
                            default: {
                                earthRowScanner.next();
                                venusRowScanner.next();
                            }
                        }
                    }
                }
                earth = new CelestialBody("Earth", new Point(earthX, earthY), earthVx, earthVy,
                        6378.137, 5.97219 * Math.pow(10, 24), 29.79);
                venus = new CelestialBody("Venus", new Point(venusX, venusY), venusVx, venusVy,
                        6051.893, 48.685 * Math.pow(10, 23), 35.021);

            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            System.exit(1);
        }

        double spaceshipOrbitRadius = earth.getRadius() + 1500;

        double distanceEarthToSun = earth.getPosition().distanceTo(sun.getPosition());

        double enx = (earth.getPosition().getX() - sun.getPosition().getX()) / distanceEarthToSun;
        double eny = (earth.getPosition().getY() - sun.getPosition().getY()) / distanceEarthToSun;

        double etx = -eny;
        double ety = enx;

        double spaceshipX = -enx * spaceshipOrbitRadius + earth.getPosition().getX() + earth.getRadius();
        double spaceshipY = -eny * spaceshipOrbitRadius + earth.getPosition().getY() + earth.getRadius();

        double spaceshipV = -7.12 - 8 + earth.getVx() * etx + earthVy * ety;
        double spaceshipVx = spaceshipV * etx;
        double spaceshipVy = spaceshipV * ety;

        try (FileWriter outFile = new FileWriter("out.txt")) {
            outFile.write(String.format("%s %.16f %.16f %.16f %.16f %.16f\n",
                    sun.getName(), sun.getPosition().getX(), sun.getPosition().getY(), sun.getVx(), sun.getVy(), sun.getRadius()));
            outFile.write(String.format("%s %.16f %.16f %.16f %.16f %.16f\n",
                    earth.getName(), earth.getPosition().getX(), earth.getPosition().getY(), earth.getVx(), earth.getVy(), earth.getRadius()));
            outFile.write(String.format("%s %.16f %.16f %.16f %.16f %.16f\n",
                    venus.getName(), venus.getPosition().getX(), venus.getPosition().getY(), venus.getVx(), venus.getVy(), venus.getRadius()));

        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }
}
