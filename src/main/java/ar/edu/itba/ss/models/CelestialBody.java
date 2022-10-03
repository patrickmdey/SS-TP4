package main.java.ar.edu.itba.ss.models;

import java.util.List;

public class CelestialBody {

    private static final double GRAVITY = 6.693 * Math.pow(10, -11);
    private final String name;
    private final Point position;
    private final double radius;
    private final double mass;
    private final double orbitalSpeed;

    private double vx;

    private double vy;

    public CelestialBody(String name, Point position, double vx, double vy, double radius, double mass, double orbitalSpeed) {
        this.name = name;
        this.position = position;
        this.radius = radius;
        this.mass = mass;
        this.orbitalSpeed = orbitalSpeed;
        this.vx = vx;
        this.vy = vy;
    }

    public double[] gravityForceWith(CelestialBody other) {
        double[] direction = new double[]{other.position.getX() - position.getX(), other.position.getY() - position.getY()};
        double distance = position.distanceTo(other.position);
        direction[0] /= distance;
        direction[1] /= distance;

        double force = GRAVITY * (mass * other.mass) / Math.pow(distance, 2);
        return new double[]{force * direction[0], force * direction[1]};
    }

    public double[] totalGravitationalForces(List<CelestialBody> bodies) {
        double totX = 0;
        double totY = 0;

        for (CelestialBody other : bodies) {
            double[] f = gravityForceWith(other);

            totX += (f[0] * (other.position.getX() - position.getX())) / Math.abs(radius - other.radius); //TODO: radio o distancia?
            totY += (f[1] * (other.position.getY() - position.getY())) / Math.abs(radius - other.radius);
        }

        return new double[]{totX, totY};
    }

    public String getName() {
        return name;
    }

    public double getRadius() {
        return radius;
    }

    public Point getPosition() {
        return position;
    }

    public double getMass() {
        return mass;
    }

    public double getOrbitalSpeed() {
        return orbitalSpeed;
    }

    public double getVx() {
        return vx;
    }

    public double getVy() {
        return vy;
    }
}