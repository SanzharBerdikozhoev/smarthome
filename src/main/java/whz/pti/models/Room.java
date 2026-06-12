package whz.pti.models;

import java.math.BigDecimal;

public class Room {
    private BigDecimal id;
    private String name;
    private String floor;
    private double square;
    private Home home;

    public Room() {}

    public Room(BigDecimal id, String name, String floor, double square, Home home) {
        this.id = id;
        this.name = name;
        this.floor = floor;
        this.square = square;
        this.home = home;
    }

    public BigDecimal getId() {
        return id;
    }

    public void setId(BigDecimal id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFloor() {
        return floor;
    }

    public void setFloor(String floor) {
        this.floor = floor;
    }

    public double getSquare() {
        return square;
    }

    public void setSquare(double square) {
        this.square = square;
    }

    public Home getHouse() {
        return home;
    }

    public void setHouse(Home home) {
        this.home = home;
    }

    @Override
    public String toString() {
        return "Room{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", floor='" + floor + '\'' +
                ", square=" + square +
                ", home=" + home +
                '}';
    }
}
