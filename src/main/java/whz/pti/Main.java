package whz.pti;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import whz.pti.services.HomeService;
import whz.pti.services.implementation.HomeServiceImpl;
import java.util.logging.Logger;


public class Main extends Application {
    private static final Logger logger = Logger.getLogger(Main.class.getName());
    private final HomeService homeService = new HomeServiceImpl();


    @Override
    public void start(Stage stage) throws Exception {
//        Optional<Home> home = houseService.getHouse(1L);
//        System.out.println(home.get());

        try{
            AnchorPane startPage = (AnchorPane) FXMLLoader.load(
                    getClass().getResource("/view/LoginPage.fxml")
            );

            Scene scene = new Scene(startPage);

            stage.setTitle("Smarthome DB");
            stage.setWidth(1600);
            stage.setHeight(900);
            stage.setScene(scene);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    public static void main(String[] args) {
        launch();
    }
}