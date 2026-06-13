package whz.pti.utils;

import whz.pti.repositories.*;
import whz.pti.repositories.implementation.*;
import whz.pti.services.*;
import whz.pti.services.implementation.*;

public class AppContext {
    private static AppContext instance;

    private final UserRepo userRepo;
    private final HomeRepo homeRepo;
    private final DeviceRepo deviceRepo;
    private final RoomRepo roomRepo;
    private final ScenarioRepo scenarioRepo;
    private final DeviceScenarioRepo deviceScenarioRepo;
    private final DeviceTypeRepo deviceTypeRepo;
    private final DeviceUserRepo deviceUserRepo;
    private final DeviceStateLogRepo deviceStateLogRepo;

    private AppContext() {
        userRepo = new UserRepoImpl();
        homeRepo = new HomeRepoImpl();
        deviceRepo = new DeviceRepoImpl();
        roomRepo = new RoomRepoImpl();
        scenarioRepo = new ScenarioRepoImpl();
        deviceScenarioRepo = new DeviceScenarioRepoImpl();
        deviceTypeRepo = new DeviceTypeRepoImpl();
        deviceUserRepo = new DeviceUserRepoImpl();
        deviceStateLogRepo = new DeviceStateLogRepoImpl();

        authService = new AuthServiceImpl(userRepo);
        homeService = new HomeServiceImpl();
        scenarioService = new ScenarioServiceImpl();
        roomService = new RoomServiceImpl(roomRepo);
        deviceService = new DeviceServiceImpl(deviceRepo);
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

    public HomeRepo getHomeRepo() {
        return homeRepo;
    }

    public UserRepo getUserRepo() {
        return userRepo;
    }

    public DeviceRepo getDeviceRepo() {
        return deviceRepo;
    }

    public RoomRepo getRoomRepo() {
        return roomRepo;
    }

    public ScenarioRepo getScenarioRepo() {
        return scenarioRepo;
    }

    public DeviceScenarioRepo getDeviceScenarioRepo() {
        return deviceScenarioRepo;
    }

    public DeviceTypeRepo getDeviceTypeRepo() {
        return deviceTypeRepo;
    }

    public DeviceUserRepo getDeviceUserRepo() {
        return deviceUserRepo;
    }

    public DeviceStateLogRepo getDeviceStateLogRepo() {
        return deviceStateLogRepo;
    }
}
