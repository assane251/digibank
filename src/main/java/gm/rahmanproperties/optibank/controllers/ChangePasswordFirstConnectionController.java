package gm.rahmanproperties.optibank.controllers;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXPasswordField;
import gm.rahmanproperties.optibank.service.AdminService;
import gm.rahmanproperties.optibank.service.ClientService;
import gm.rahmanproperties.optibank.utils.JavaMailer;
import gm.rahmanproperties.optibank.utils.Popup;
import javafx.fxml.FXML;
import javafx.stage.Stage;
import lombok.Setter;

import javax.mail.MessagingException;
import java.util.Objects;

public class ChangePasswordFirstConnectionController {

    String email = System.getenv("email");
    String password = System.getenv("password");

    AdminService adminService = new AdminService();
    ClientService clientService = new ClientService();
    JavaMailer javaMailer = new JavaMailer(email, password);

    @FXML
    private JFXPasswordField newPasswordField;

    @FXML
    private JFXPasswordField confirmPasswordField;

    @FXML
    private JFXButton saveButton;

    @Setter
    private String username;

    @FXML
    public void handleSavePassword() throws MessagingException {
        String newPassword = newPasswordField.getText();
        String confirmPassword = confirmPasswordField.getText();

        if (newPassword.isEmpty() || confirmPassword.isEmpty()) {
            Popup.showErrorMessage("Les champs ne peuvent pas être vides.");
            return;
        }

        if (!newPassword.equals(confirmPassword)) {
            Popup.showErrorMessage("Les mots de passe ne correspondent pas.");
            return;
        }

        if (Objects.equals(username, "admin")) {
            adminService.updateAdminPassword(username, newPassword);
        } else {
            clientService.updateClientPassword(username, newPassword);
            javaMailer.sendEmail(username, "Modification de mot de passe", "Vous avez modifier votre mot de passe\nUsername: " + "<strong>" +
                    username+"</strong>\nMot de passe: " + "<strong>"+newPassword+"</strong>\n");
        }

        Stage stage = (Stage) saveButton.getScene().getWindow();
        stage.close();
    }
}