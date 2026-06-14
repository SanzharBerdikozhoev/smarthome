package whz.pti.models;

import whz.pti.repositories.implementation.DeviceRepoImpl;
import whz.pti.repositories.implementation.ScenarioRepoImpl;
import whz.pti.utils.annotations.ForeignKey;

public class DeviceScenario {
    @ForeignKey(column = "device_id", repoClass = DeviceRepoImpl.class)
    private Device device;
    @ForeignKey(column = "automation_id", repoClass = ScenarioRepoImpl.class)
    private Scenario scenario;
    private DeviceScenarioRole role;

    public DeviceScenario() {}

    public DeviceScenario(Long id, Device device, Scenario scenario, DeviceScenarioRole role) {
        this.device = device;
        this.scenario = scenario;
        this.role = role;
    }

    public DeviceScenarioRole getRole() {
        return role;
    }

    public void setRole(DeviceScenarioRole role) {
        this.role = role;
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
        String deviceName = (this.device != null) ? this.device.toString() : "Unbekanntes Gerät";
        String scenarioName = (this.scenario != null) ? this.scenario.toString() : "Unbekanntes Szenario";

        return deviceName + " ➔ " + scenarioName;
    }
}
