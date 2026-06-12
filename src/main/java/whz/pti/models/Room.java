package whz.pti.models;

public class Room {
    private Long id;
    private String name;
    private String floor;
    private double square;
    private House house;

    public Room() {}

    public Room(Long id, String name, String floor, double square, House house) {
        this.id = id;
        this.name = name;
        this.floor = floor;
        this.square = square;
        this.house = house;
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

    public House getHouse() {
        return house;
    }

    public void setHouse(House house) {
        this.house = house;
    }

    @Override
    public String toString() {
        return "Room{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", floor='" + floor + '\'' +
                ", square=" + square +
                ", house=" + house +
                '}';
    }
}
