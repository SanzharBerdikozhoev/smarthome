package whz.pti.models;

public class DeviceType {
    private Long id;
    private String name;
    private String description;

    public DeviceType() {}

    public DeviceType(Long id, String name, String description) {
        this.id = id;
        this.name = name;
        this.description = description;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        String displayName = (this.name != null && !this.name.trim().isEmpty()) ? this.name : "Unbekannt";
        String displayId = (this.id != null) ? String.valueOf(this.id) : "Neu";

        return displayName + " (ID: " + displayId + ")";
    }
}

