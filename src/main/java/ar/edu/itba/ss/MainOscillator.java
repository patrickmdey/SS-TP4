package main.java.ar.edu.itba.ss;

import java.io.FileWriter;
import java.io.IOException;

public class MainOscillator {
    public static void main(String[] args) throws IOException {
        double mass = 70 * Math.pow(10, 3);
        double k = Math.pow(10, 4);
        double gamma = 100 * Math.pow(10, 3);
        double step = 1;

        DampedOscillator oscillator = new DampedOscillator(mass, k, gamma, step);

        oscillator.verlet();
        oscillator.beeman();
    }
}
