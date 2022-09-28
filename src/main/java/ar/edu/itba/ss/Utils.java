package main.java.ar.edu.itba.ss;

public class Utils {

    public static double eulerR(double r, double v, double step, double mass, double f) {
        return r + step * v + step * step * f / (2 * mass);
    }

    // TODO: parece que no se usa
    public static double eulerV(double v, double step, double mass, double f) {
        return v + (step * f) / mass;
    }

    public static double verletR(double currR, double prevR, double step, double mass, double f) {
        return 2 * currR - prevR + (Math.pow(step, 2) * f )/ mass;
    }

    public static double beemanR(double r, double v, double step, double currA, double prevA) {
        return r + v * step + (4 * currA - prevA) * step * step / 6;
    }

    public static double beemanV(double v, double step, double currA, double prevA, double nextA) {
        return v + (2 * nextA + 5 * currA - prevA) * step / 6;
    }

    public static double beemanPredV(double v, double step, double currA, double prevA) {
        return v + (3 * currA - prevA) * step / 2;
    }
}
