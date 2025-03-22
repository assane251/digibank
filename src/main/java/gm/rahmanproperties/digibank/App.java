package gm.rahmanproperties.digibank;

import gm.rahmanproperties.digibank.service.AdminService;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class App extends Application {

    private final AdminService adminService = new AdminService();

    @Override
    public void start(Stage stage) throws IOException {

        adminService.ensureDefaultAdminExists();

        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource("/fxml/connexion.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setTitle("Digi Bank");
        stage.setResizable(false);
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}