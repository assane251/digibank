package gm.rahmanproperties.digibank.controllers;

import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;
import gm.rahmanproperties.digibank.domain.Admin;
import gm.rahmanproperties.digibank.domain.Client;
import gm.rahmanproperties.digibank.enums.Status;
import gm.rahmanproperties.digibank.service.AdminService;
import gm.rahmanproperties.digibank.service.ClientService;
import gm.rahmanproperties.digibank.service.UserSession;
import gm.rahmanproperties.digibank.utils.Popup;
import gm.rahmanproperties.digibank.utils.WindowManager;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.mindrot.jbcrypt.BCrypt;

import java.io.IOException;
import java.util.Objects;

public class ConnexionController {

    @FXML
    private JFXPasswordField txtMotDePasse;

    @FXML
    private JFXTextField txtUsername;

    private final AdminService adminService = new AdminService();
    private final ClientService clientService = new ClientService();

    @FXML
    private void btnMotDePasseOublie(ActionEvent event) throws IOException {
        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.setTitle("Mot de passe oublié");

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/client/forgot_password.fxml"));
        if (fxmlLoader.getLocation() == null) {
            throw new IOException("Le fichier FXML /fxml/client/forgot_password.fxml n'a pas été trouvé.");
        }
        Scene scene = new Scene(fxmlLoader.load());
        dialog.setScene(scene);
        dialog.showAndWait();
    }

    @FXML
    void btnCreerCompte(ActionEvent event) throws IOException {
        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.setTitle("Creer Client");

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/client/create_client_account.fxml"));
        if (fxmlLoader.getLocation() == null) {
            throw new IOException("Le fichier FXML /fxml/client/create_client_account.fxml n'a pas été trouvé.");
        }
        Scene scene = new Scene(fxmlLoader.load());
        dialog.setScene(scene);
        dialog.showAndWait();
    }

    @FXML
    void btnSeConnecter(ActionEvent event) {
        String username = txtUsername.getText().trim();
        String password = txtMotDePasse.getText().trim();

        Admin admin = null;
        Client client = null;

        if (username.isEmpty() || password.isEmpty()) {
            Popup.showErrorMessage("Veuillez saisir un identifiant et un mot de passe.");
            return;
        } else if (Objects.equals(username, "admin")) {
            admin = adminService.authenticateAdmin(username);
        } else {
            client = clientService.existByEmail(username);
        }


        Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();

        if (admin != null && BCrypt.checkpw(password, admin.getPassword())) {
            if (admin.isFirstLogin()) {
                try {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/change_password_first_connection.fxml"));
                    Scene scene = new Scene(loader.load());
                    ChangePasswordFirstConnectionController controller = loader.getController();
                    controller.setUsername(username);
                    Stage stage = new Stage();
                    stage.setScene(scene);
                    stage.setTitle("Changer le mot de passe");
                    stage.showAndWait();

                    admin = adminService.authenticateAdmin(username);
                    if (admin.isFirstLogin()) {
                        Popup.showErrorMessage("Vous devez changer votre mot de passe avant de continuer.");
                        return;
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            WindowManager.closeAllAndOpen("/fxml/admin/dashboard_admin.fxml", currentStage, "Tableau de bord Admin");
            return;
        } else if (client != null && BCrypt.checkpw(password, client.getPassword())) {

            if (client.isFirstLogin()) {
                try {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/change_password_first_connection.fxml"));
                    Scene scene = new Scene(loader.load());
                    ChangePasswordFirstConnectionController controller = loader.getController();
                    controller.setUsername(username);
                    Stage stage = new Stage();
                    stage.setScene(scene);
                    stage.setTitle("Changer le mot de passe");
                    stage.showAndWait();

                    client = clientService.existByEmail(username);
                    if (client.isFirstLogin()) {
                        Popup.showErrorMessage("Vous devez changer votre mot de passe avant de continuer.");
                        return;
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            if (client.getStatus() == Status.ACTIF) {
                UserSession.getInstance().setLoggedInClient(client);

                WindowManager.closeAllAndOpen("/fxml/client/dashboard_client.fxml", currentStage, "Tableau de bord Client");
                return;
            } else if (client.getStatus() == Status.EN_ATTENTE ){
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("En attente de validation");
                alert.setHeaderText("Compte En Attente");
                alert.setContentText("Votre compte est en attente.");
                alert.showAndWait();
                return;
            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Compte Bloque");
                alert.setHeaderText("Compte Bloque");
                alert.setContentText("Votre compte a été bloqué.");
                alert.showAndWait();
                return;
            }
        }

        Popup.showErrorMessage("Identifiant ou mot de passe incorrect.");
    }
}