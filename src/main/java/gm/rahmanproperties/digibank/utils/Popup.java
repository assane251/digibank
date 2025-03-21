package gm.rahmanproperties.digibank.utils;

import io.github.hugoquinn2.fxpopup.constants.MessageType;
import io.github.hugoquinn2.fxpopup.constants.Theme;
import io.github.hugoquinn2.fxpopup.controller.FxPopup;
import io.github.hugoquinn2.fxpopup.model.Message;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;

import java.util.concurrent.TimeUnit;

public class Popup {

    private static final FxPopup fxPopup = new FxPopup();

    public static void showErrorMessage(String message) {
        Message errorMessage = new Message("Erreur", MessageType.ERROR, (int) TimeUnit.SECONDS.toSeconds(5));
        errorMessage.setContext(message);
        fxPopup.setPos(Pos.TOP_RIGHT);
        fxPopup.setTheme(Theme.LIGHT);
        fxPopup.add(errorMessage);
    }

    public static void showSuccessMessage(String message) {
        Message successMessage = new Message("Succès", MessageType.SUCCESS, 5);
        successMessage.setContext(message);
        fxPopup.setPos(Pos.TOP_RIGHT);
        fxPopup.setTheme(Theme.LIGHT);
        fxPopup.add(successMessage);
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Erreur");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showSuccess(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Succès");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
