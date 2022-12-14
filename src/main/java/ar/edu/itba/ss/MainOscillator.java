package main.java.ar.edu.itba.ss;

import main.java.ar.edu.itba.ss.oscillator.DampedOscillator;

import java.io.FileWriter;
import java.io.IOException;

public class MainOscillator {
    public static void main(String[] args) throws IOException {
        double mass = 70;
        double k = Math.pow(10, 4);
        double gamma = 100 ;
        double step = args[0] != null ? Double.parseDouble(args[0]) : 0.01;

        FileWriter writer = new FileWriter("./outFiles/damped_out.txt");
        DampedOscillator oscillator = new DampedOscillator(mass, k, gamma, step, writer);

        oscillator.verlet();
        oscillator.beeman();
        oscillator.gear();
        writer.close();
    }
}
