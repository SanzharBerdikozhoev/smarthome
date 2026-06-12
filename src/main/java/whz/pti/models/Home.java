package whz.pti.models;

import java.math.BigDecimal;

public class Home {
    private Long id;
    private String address;
    private String town;
    private String zipCode;
    private User user;

    public Home() {}

    public Home(Long id, String address, String town, String zipCode, User user) {
        this.id = id;
        this.address = address;
        this.town = town;
        this.zipCode = zipCode;
        this.user = user;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getTown() {
        return town;
    }

    public void setTown(String town) {
        this.town = town;
    }

    public String getZipCode() {
        return zipCode;
    }

    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @Override
    public String
    toString() {
        return "Home{" +
                "id=" + id +
                ", address='" + address + '\'' +
                ", town='" + town + '\'' +
                ", zipCode='" + zipCode + '\'' +
                ", user=" + user +
                '}';
    }
}
