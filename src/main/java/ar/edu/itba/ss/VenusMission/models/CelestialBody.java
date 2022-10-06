package main.java.ar.edu.itba.ss.VenusMission.models;

import java.util.List;

public class CelestialBody {
    private static final double GRAVITY = 6.693 * Math.pow(10, -20);
    private final String name;
    private final int id;
    private final Point position;
    private final double radius;
    private final double mass;
    private final double orbitalSpeed;
    private double vx;
    private double vy;

    public CelestialBody(int id, String name, Point position, double vx, double vy, double radius, double mass, double orbitalSpeed) {
        this.id = id;
        this.name = name;
        this.position = position;
        this.radius = radius;
        this.mass = mass;
        this.orbitalSpeed = orbitalSpeed;
        this.vx = vx;
        this.vy = vy;
    }

    public double[] gravityForceWith(CelestialBody other) {
        double[] direction = new double[]{other.getPosition().getX() - position.getX(),
                other.getPosition().getY() - position.getY()};
        double distance = position.distanceTo(other.getPosition());
        direction[0] /= distance;
        direction[1] /= distance;

        double force = GRAVITY * (mass * other.getMass()) / Math.pow(distance, 2);
        return new double[]{force * direction[0], force * direction[1]};
    }

    public double[] totalGravitationalForces(List<CelestialBody> bodies) {
        double totX = 0;
        double totY = 0;

        for (CelestialBody other : bodies) {
            double[] f = gravityForceWith(other);

            totX += f[0];
            totY += f[1];
        }
        return new double[]{totX, totY};
    }

    public void updateVelocity(double vx, double vy){
        this.vx = vx;
        this.vy = vy;
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

    public int getId() {
        return id;
    }
}