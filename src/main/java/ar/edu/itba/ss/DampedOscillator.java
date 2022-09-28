package main.java.ar.edu.itba.ss;

import javax.rmi.CORBA.Util;
import java.io.FileWriter;
import java.io.IOException;

public class DampedOscillator {
    private double mass;
    private double k;
    private double gamma;
    private double r0;
    private double v0;
    private static double A = 0.1; // TODO: preguntar

    private double step;
    private static final double EPSILON = 1 * Math.pow(10, -5);
    private FileWriter writer;

    public DampedOscillator(double mass, double k, double gamma, double step) throws IOException {
        this.mass = mass;
        this.k = k;
        this.gamma = gamma;
        this.r0 = 1;
        this.v0 = -A * gamma / (2 * mass);
        this.step = step;
        writer = new FileWriter("verlet.txt");
    }

    public void analitic(double t) {
        double r;
        while (Double.compare(Math.abs(t - 5), EPSILON) < 0) {
            r = A * Math.exp(-(gamma / (2 * mass) * t)) * Math.cos(Math.pow(((k / mass) - (gamma * gamma / (4 * mass * mass))), 0.5) * t);
        }
    }

    public void verlet() throws IOException {
        double currR = r0;

        double prevR = Utils.eulerR(r0, v0, -step, mass, f(r0, v0));

        double t = 0;

        double nextR;
        double currV = v0;

        writer.write(String.format("%f %f\n", t, currR));
        while (Double.compare(Math.abs(t - 5), EPSILON) < 0) {
            nextR = Utils.verletR(currR, prevR, step, mass, f(currR, currV));
            currV = (nextR - prevR) / 2 * step; // TODO capaz pasarla a utils
            prevR = currR;
            currR = nextR;
            t += step;
            writer.write(String.format("%f %f\n", t, currR));
        }
    }

    public void beeman() throws IOException {
        double currR = r0;
        double currV = v0;
        double prevR = Utils.eulerR(r0, v0, -step, mass, f(r0, v0));
        double prevV = Utils.eulerV(v0, -step, mass, f(r0, v0));

        double t = 0;

        writer.write(String.format("%f %f\n", t, currR));
        double nextR, nextV, currA, prevA, nextA;
        while (Double.compare(Math.abs(t - 5), EPSILON) < 0) {

            currA = f(currR, currV) / mass;
            prevA = f(prevR, prevV) / mass;

            nextR = Utils.beemanR(currR, currV, step, currA, prevA);

            double predV = Utils.beemanPredV(currV, step, currA, prevA);

            nextA = f(nextR, predV) / mass;

            nextV = Utils.beemanV(currV, step, currA, prevA, nextA); //TODO: next A como se calcula? ver si se usa predV
            prevR = currR;
            currR = nextR;

            prevV = currV;
            currV = nextV;
            t += step;
            writer.write(String.format("%f %f\n", t, currR));
        }
    }

    private double f(double r, double v) {
        return (-k * r) - (gamma * v);
    }
}
