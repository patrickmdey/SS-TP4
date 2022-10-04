package main.java.ar.edu.itba.ss.utils;

public class IntegrationAlgorithms {

    public static double eulerR(double r, double v, double step, double mass, double f) {
        return r + step * v + step * step * f / (2 * mass);
    }

    public static double eulerV(double v, double step, double mass, double f) {
        return v + (step * f) / mass;
    }

    public static double verletR(double currR, double r, double step, double mass, double f) {
        return 2 * currR - r + (Math.pow(step, 2) * f) / mass;
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

    public static void gearPredR(double[] r, double step) {
        double[] oldR = new double[r.length];
        System.arraycopy(r, 0, oldR, 0, r.length);

        r[4] = oldR[5] * step + oldR[4];
        r[3] = oldR[5] * Math.pow(step, 2) / 2 + oldR[4] * step + oldR[3];
        r[2] = oldR[5] * Math.pow(step, 3) / 6 + oldR[4] * Math.pow(step, 2) / 2
                + oldR[3] * step + oldR[2];

        r[1] = oldR[5] * Math.pow(step, 4) / 24 + oldR[4] * Math.pow(step, 3) / 6
                + oldR[3] * Math.pow(step, 2) / 2 + oldR[2] * step + oldR[1];

        r[0] = oldR[5] * Math.pow(step, 5) / 120 + oldR[4] * Math.pow(step, 4) / 24
                + oldR[3] * Math.pow(step, 3) / 6 + oldR[2] * Math.pow(step, 2) / 2
                + oldR[1] * step + oldR[0];
    }

    public static int[] factorials = new int[]{1, 1, 2, 6, 24, 120};

    public static void gearCorrectR(double[] alphas, double[] r, double deltaR2, double step) {
        for (int i = 0; i < 6; i++) {
            r[i] += alphas[i] * deltaR2 * factorials[i] / Math.pow(step, i);
        }
    }
}
