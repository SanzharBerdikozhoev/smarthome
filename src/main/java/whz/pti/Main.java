package whz.pti;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import whz.pti.models.Home;
import whz.pti.repositories.HouseRepo;
import whz.pti.services.HouseService;
import whz.pti.services.implementation.HouseServiceImpl;
import whz.pti.utils.DBConnection;

import java.util.Optional;
import java.util.logging.Logger;


public class Main extends Application {
    private static final Logger logger = Logger.getLogger(Main.class.getName());
    private HouseService houseService = new HouseServiceImpl();


    @Override
    public void start(Stage stage) throws Exception {
//        Optional<Home> home = houseService.getHouse(1L);
//        System.out.println(home.get());

        try{
            testDatabaseConnection();
            AnchorPane startPage = (AnchorPane) FXMLLoader.load(
                    getClass().getResource("/view/LoginPage.fxml")
            );

            stage.setTitle("Smarthome DB");
            stage.setScene(new Scene(startPage));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }


    private void testDatabaseConnection() {
        try {
            DBConnection dbConnection = DBConnection.getInstance();
            dbConnection.getConnection();
            logger.info("Database connection successful");
        } catch (Exception e) {
            logger.warning("Database connection test failed: " + e.getMessage());
            System.err.println("WARNING: Could not connect to database. Make sure SmarthomeDB is created and accessible.");
            e.printStackTrace();
        }
    }


    public static void main(String[] args) {
        launch();
    }
}