package gm.rahmanproperties.digibank.controllers.client;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;
import gm.rahmanproperties.digibank.domain.Client;
import gm.rahmanproperties.digibank.service.ClientService;
import gm.rahmanproperties.digibank.utils.JavaMailer;
import gm.rahmanproperties.digibank.utils.Popup;
import javafx.fxml.FXML;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.mindrot.jbcrypt.BCrypt;

import javax.mail.MessagingException;

public class ForgotPasswordController {

    String email = System.getenv("email");
    String password = System.getenv("password");

    JavaMailer javaMailer = new JavaMailer(email, password);
    ClientService clientService = new ClientService();

    @FXML
    private VBox step1;

    @FXML
    private VBox step2;

    @FXML
    private JFXTextField emailField;

    @FXML
    private JFXPasswordField newPasswordField;

    @FXML
    private JFXPasswordField confirmPasswordField;

    @FXML
    private JFXButton resetButton;


    @FXML
    public void handleNextStep() {
        email = emailField.getText();

        if (email.isEmpty()) {
            Popup.showErrorMessage("Veuillez entrer votre adresse email.");
            return;
        }

        Client clientExists = clientService.existByEmail(email);

        if (clientExists.getEmail() != null) {
            step1.setVisible(false);
            step1.setManaged(false);
            step2.setVisible(true);
            step2.setManaged(true);
        } else {
            Popup.showErrorMessage("Aucun compte associé à cet email.");
        }
    }

    @FXML
    public void handleResetPassword() throws MessagingException {
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

        String hashedPassword = BCrypt.hashpw(newPassword, BCrypt.gensalt());

        clientService.updateClientPassword(email, hashedPassword);
        javaMailer.sendEmail(email,
                "Réinitialisation de votre mot de passe Digibank",
                "Votre mot de passe a été réinitialisé.\n\nNouveau mot de passe: " + newPassword
        );

        Popup.showSuccessMessage("Mot de passe mis à jour. Un email de confirmation a été envoyé à " + email + " (simulation).");

        Stage stage = (Stage) resetButton.getScene().getWindow();
        stage.close();
    }
}