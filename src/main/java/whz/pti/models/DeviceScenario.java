package whz.pti.models;

public class DeviceScenario {
    private Device device;
    private Scenario scenario;

    public DeviceScenario() {}

    public DeviceScenario(Long id, Device device, Scenario scenario) {
        this.device = device;
        this.scenario = scenario;
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
                ", device=" + device +
                ", scenario=" + scenario +
                '}';
    }
}
