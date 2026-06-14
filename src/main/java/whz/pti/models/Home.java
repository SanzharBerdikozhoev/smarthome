package whz.pti.models;

import whz.pti.repositories.implementation.UserRepoImpl;
import whz.pti.utils.annotations.Column;
import whz.pti.utils.annotations.ForeignKey;

public class Home {
    private Long id;
    private String address;
    private String town;
    @Column(name = "zip_code")
    private String zip_code;
    @ForeignKey(column = "user_id", repoClass = UserRepoImpl.class)
    private User user;

    public Home() {}

    public Home(Long id, String address, String town, String zipCode, User user) {
        this.id = id;
        this.address = address;
        this.town = town;
        this.zip_code = zipCode;
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

    public String getZip_code() {
        return zip_code;
    }

    public void setZip_code(String zip_code) {
        this.zip_code = zip_code;
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
        return address + "," + town;
    }
}
