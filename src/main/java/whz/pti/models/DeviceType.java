package whz.pti.models;

import java.math.BigDecimal;

public class DeviceType {
    private BigDecimal id;
    private String name;
    private String description;

    public DeviceType() {}

    public DeviceType(BigDecimal id, String name, String description) {
        this.id = id;
        this.name = name;
        this.description = description;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return "DeviceType{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                '}';
    }
}

