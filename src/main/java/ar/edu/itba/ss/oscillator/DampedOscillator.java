package main.java.ar.edu.itba.ss.oscillator;

import main.java.ar.edu.itba.ss.utils.IntegrationAlgorithms;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Locale;

public class DampedOscillator {
    private static final double A = 1;
    private static final double EPSILON = 1 * Math.pow(10, -2);
    private final double mass;
    private final double k;
    private final double gamma;
    private final double r0;
    private final double v0;
    private final double step;
    private final FileWriter writer;

    public DampedOscillator(double mass, double k, double gamma, double step, FileWriter writer) throws IOException {
        this.mass = mass;
        this.k = k;
        this.gamma = gamma;
        this.r0 = 1;
        this.v0 = -A * gamma / (2 * mass);
        this.step = step;
        this.writer = writer;
    }

    public void verlet() throws IOException {
        double currR = r0;

        double prevR = IntegrationAlgorithms.eulerR(r0, v0, -step, mass, f(r0, v0));

        double t = 0;

        double nextR;
        double currV = v0;

        writer.write(String.format(Locale.ROOT, "%.16f %.16f\n", t, currR));
        while (t < 5) {
            nextR = IntegrationAlgorithms.verletR(currR, prevR, step, mass, f(currR, currV));
            currV = (nextR - prevR) / (2 * step);
            prevR = currR;
            currR = nextR;
            t += step;
            writer.write(String.format(Locale.ROOT, "%.16f %.16f\n", t, currR));
        }
        writer.write("\n");
        writer.flush();
    }

    public void beeman() throws IOException {
        double currR = r0;
        double currV = v0;
        double prevR = IntegrationAlgorithms.eulerR(r0, v0, -step, mass, f(r0, v0));
        double prevV = IntegrationAlgorithms.eulerV(v0, -step, mass, f(r0, v0));

        double t = 0;

        writer.write(String.format(Locale.ROOT, "%.16f %.16f\n", t, currR));
        double nextR, nextV, currA, prevA, nextA;
        while (t < 5) {

            currA = f(currR, currV) / mass;
            prevA = f(prevR, prevV) / mass;

            nextR = IntegrationAlgorithms.beemanR(currR, currV, step, currA, prevA);

            double predV = IntegrationAlgorithms.beemanPredV(currV, step, currA, prevA);

            nextA = f(nextR, predV) / mass;

            nextV = IntegrationAlgorithms.beemanV(currV, step, currA, prevA, nextA);
            prevR = currR;
            currR = nextR;

            prevV = currV;
            currV = nextV;
            t += step;
            writer.write(String.format(Locale.ROOT, "%.16f %.16f\n", t, currR));
        }
        writer.write("\n");
        writer.flush();

//        writer.close();
    }

    public void gear() throws IOException {

        double[] alphas = new double[]{3.0 / 16, 251.0 / 360, 1, 11.0 / 18, 1.0 / 6, 1.0 / 60};

        double t = 0;

        writer.write(String.format(Locale.ROOT, "%.16f %.16f\n", t, r0));
        double currA;

        double[] r = new double[6];
        r[0] = r0;
        r[1] = v0;
        r[2] = f(r[0], r[1]) / mass;
        r[3] = f(r[1], r[2]) / mass;
        r[4] = f(r[2], r[3]) / mass;
        r[5] = f(r[3], r[4]) / mass;


        while (t < 5) {
            // Predecir
            IntegrationAlgorithms.gearPredR(r, step);

            // Evaluar
            currA = f(r[0], r[1]) / mass;
            double deltaA = currA - r[2];
            double deltaR2 = deltaA * Math.pow(step, 2) / 2;

            // Corregir
            IntegrationAlgorithms.gearCorrectR(alphas, r, deltaR2, step);

            t += step;
            writer.write(String.format(Locale.ROOT, "%.16f %.16f\n", t, r[0]));
        }

        writer.write("\n");
        writer.flush();
    }

    private double f(double r, double v) {
        return (-k * r) - (gamma * v);
    }
}
