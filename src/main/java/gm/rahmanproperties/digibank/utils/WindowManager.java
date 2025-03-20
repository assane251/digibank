package gm.rahmanproperties.digibank.utils;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.Window;

import java.io.IOException;
import java.util.List;

public class WindowManager {
    public static void closeAllAndOpen(String fxmlPath, Stage currentStage, String title) {
        try {
            // Get all open stages
            List<Window> stagesToClose = Stage.getWindows().filtered(window -> window instanceof Stage);

            // Close all stages except the current one
            for (Window window : stagesToClose) {
                if (window != currentStage && window instanceof Stage stage) {
                    stage.close();
                }
            }

            // Load the new FXML
            FXMLLoader loader = new FXMLLoader(WindowManager.class.getResource(fxmlPath));
            if (loader.getLocation() == null) {
                throw new IOException("FXML file not found: " + fxmlPath);
            }
            Parent root = loader.load();

            // Create and configure the new stage
            Stage newStage = new Stage();
            newStage.setScene(new Scene(root));
            newStage.setTitle(title);
            newStage.setResizable(false);
            newStage.show();

            // Close the current stage after the new one is shown
            if (currentStage != null) {
                currentStage.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
            Popup.showErrorMessage("Erreur lors du chargement de la fenêtre : " + fxmlPath);
        }
    }

    public static void openParentAndChild(String childFxmlPath, String title) {
        try {
            // Load the parent FXML
//            FXMLLoader parentLoader = new FXMLLoader(WindowManager.class.getResource(parentFxmlPath));
//            if (parentLoader.getLocation() == null) {
//                throw new IOException("Parent FXML file not found: " + parentFxmlPath);
//            }
//            Parent parentRoot = parentLoader.load();
//            Stage parentStage = new Stage();
//            parentStage.setScene(new Scene(parentRoot));
//            parentStage.setTitle(title);
//            parentStage.setResizable(false);

            // Load the child FXML
            FXMLLoader childLoader = new FXMLLoader(WindowManager.class.getResource(childFxmlPath));
            if (childLoader.getLocation() == null) {
                throw new IOException("Child FXML file not found: " + childFxmlPath);
            }
            Parent childRoot = childLoader.load();
            Stage childStage = new Stage();
            childStage.setScene(new Scene(childRoot));
            childStage.setTitle(title);
            childStage.setResizable(false);

            // Link parent and child lifecycle
//            parentStage.setOnCloseRequest(event -> childStage.close());
//
//            // Show both stages
//            parentStage.show();
            childStage.show();
        } catch (IOException e) {
            e.printStackTrace();
            Popup.showErrorMessage("Erreur lors de l'ouverture des fenêtres : " + e.getMessage());
        }
    }
}