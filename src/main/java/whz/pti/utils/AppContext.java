package whz.pti.utils;

import whz.pti.services.*;
import whz.pti.services.implementation.*;

public class AppContext {
    private static AppContext instance;

    private AppContext() {
        authService = new AuthServiceImpl();
        homeService = new HomeServiceImpl();
        scenarioService = new ScenarioServiceImpl();
        roomService = new RoomServiceImpl();
        deviceService = new DeviceServiceImpl();
        deviceTypeService = new DeviceTypeServiceImpl();
        deviceScenarioService = new DeviceScenarioServiceImpl();
        deviceUserService = new DeviceUserServiceImpl();
        deviceStateLogService = new DeviceStateLogServiceImpl();
    }

    public static AppContext getInstance() {
        if(instance == null) {
            synchronized (AppContext.class) {
                if(instance == null) {
                    instance = new AppContext();
                }
            }
        }

        return instance;
    }

    private final AuthService authService;
    private final HomeService homeService;
    private final ScenarioService scenarioService;
    private final RoomService roomService;
    private final DeviceService deviceService;
    private final DeviceTypeService deviceTypeService;
    private final DeviceScenarioService deviceScenarioService;
    private final DeviceUserService deviceUserService;
    private final DeviceStateLogService deviceStateLogService;

    public AuthService getAuthService() {
        return authService;
    }

    public HomeService getHomeService() {
        return homeService;
    }

    public ScenarioService getScenarioService() {
        return scenarioService;
    }

    public RoomService getRoomService() {
        return roomService;
    }

    public DeviceService getDeviceService() {
        return deviceService;
    }

    public DeviceTypeService getDeviceTypeService() {
        return deviceTypeService;
    }

    public DeviceScenarioService getDeviceScenarioService() {
        return deviceScenarioService;
    }

    public DeviceUserService getDeviceUserService() {
        return deviceUserService;
    }

    public DeviceStateLogService getDeviceStateLogService() {
        return deviceStateLogService;
    }
}
