package main.java.ar.edu.itba.ss;

import main.java.ar.edu.itba.ss.models.CelestialBody;
import main.java.ar.edu.itba.ss.models.Point;
import main.java.ar.edu.itba.ss.utils.FileReader;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;


public class Main {
    public static void main(String[] args) {

        double earthX = 0, earthY = 0, venusX = 0, venusY = 0;
        try (Scanner scanner = new Scanner(new File("../files/earth-horizons.csv")); Scanner venusScanner = new Scanner(new File("../files/venus-horizons.csv"))) {
            CelestialBody sun = new CelestialBody("Sun", new Point(0, 0), 6.9551 * Math.pow(10, 5), 1988500 * Math.pow(10, 24), 0);

            //Read line
            while (scanner.hasNextLine() && venusScanner.hasNextLine()) {
                String line = scanner.nextLine();
                String venusLine = scanner.nextLine();
                //Scan the line for tokens
                try (Scanner rowScanner = new Scanner(line); Scanner venusRowScanner = new Scanner(venusLine)) {
                    rowScanner.useDelimiter(",");
                    for (int i = 0; rowScanner.hasNext(); i++) {
                        if (i == 2) {
                            earthX = rowScanner.nextDouble();
                            venusX = venusRowScanner.nextDouble();
                        }
                        if (i == 3) {
                            earthY = rowScanner.nextDouble();
                            venusY = venusRowScanner.nextDouble();
                        }
                    }
                }
                CelestialBody earth = new CelestialBody("Earth", new Point(earthX, earthY), 6378.137, 5.97219 * Math.pow(10, 24), 29.79);
                CelestialBody venus = new CelestialBody("Venus", new Point(venusX, venusY), 6051.893, 48.685 * Math.pow(10, 23), 35.021);

            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }



    }
}
