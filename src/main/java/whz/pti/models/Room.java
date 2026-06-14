package whz.pti.models;

import whz.pti.repositories.implementation.HomeRepoImpl;
import whz.pti.utils.annotations.ForeignKey;

public class Room {
    private Long id;
    private String name;
    private String floor;
    private double square;
    @ForeignKey(column = "home_id", repoClass = HomeRepoImpl.class)
    private Home home;

    public Room() {}

    public Room(Long id, String name, String floor, double square, Home home) {
        this.id = id;
        this.name = name;
        this.floor = floor;
        this.square = square;
        this.home = home;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
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
        return name + " " + floor;
    }
}
