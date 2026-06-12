package whz.pti.models;

import java.math.BigDecimal;

public class DeviceScenario {
    private BigDecimal id;
    private Device device;
    private Scenario scenario;

    public DeviceScenario() {}

    public DeviceScenario(BigDecimal id, Device device, Scenario scenario) {
        this.id = id;
        this.device = device;
        this.scenario = scenario;
    }

    public BigDecimal getId() {
        return id;
    }

    public void setId(BigDecimal id) {
        this.id = id;
    }

    public Device getDevice() {
        return device;
    }

    public void setDevice(Device device) {
        this.device = device;
    }

    public Scenario getScenario() {
        return scenario;
    }

    public void setScenario(Scenario scenario) {
        this.scenario = scenario;
    }

    @Override
    public String toString() {
        return "DeviceScenario{" +
                "id=" + id +
                ", device=" + device +
                ", scenario=" + scenario +
                '}';
    }
}
