package gm.rahmanproperties.optibank.controllers.client;

import com.jfoenix.controls.JFXTextField;
import gm.rahmanproperties.optibank.domain.Client;
import gm.rahmanproperties.optibank.dtos.ClientDto;
import gm.rahmanproperties.optibank.enums.Status;
import gm.rahmanproperties.optibank.service.ClientService;
import gm.rahmanproperties.optibank.utils.JavaMailer;
import gm.rahmanproperties.optibank.utils.Popup;
import javafx.fxml.FXML;

import java.time.LocalDate;

public class CreateClientAccountController {

    @FXML private JFXTextField nomField;
    @FXML private JFXTextField prenomField;
    @FXML private JFXTextField emailField;
    @FXML private JFXTextField telephoneField;

    String email = System.getenv("email");
    String password = System.getenv("password");

    ClientService clientService = new ClientService();
    JavaMailer javaMailer = new JavaMailer(email, password);

    @FXML
    public void handleCreateAccount() {
        if (!validateInputs()) {
            return;
        }

        try {
            ClientDto clientDto = ClientDto.builder()
                    .nom(nomField.getText())
                    .prenom(prenomField.getText())
                    .email(emailField.getText())
                    .telephone(telephoneField.getText())
                    .status(Status.EN_ATTENTE)
                    .password("passer123")
                    .dateInscription(LocalDate.now())
                    .firstLogin(true)
                    .build();

            Client client = clientService.saveClient(clientDto);
            Popup.showSuccessMessage("Client créé avec succès!");
            javaMailer.sendEmail(email, client.getEmail(), "Creation de avec success "
                    + client.getEmail() + "en attente d'acceptation de l'admin");
            clearFields();

        } catch (NumberFormatException e) {
            Popup.showErrorMessage("Le solde initial doit être un nombre valide (ex: 1000.00).");
        } catch (IllegalArgumentException e) {
            Popup.showSuccessMessage("Erreur" + e.getMessage());
        } catch (Exception e) {
            Popup.showSuccessMessage("Erreur inattendue: " + e.getMessage());
        }
    }

    private boolean validateInputs() {
        if (nomField.getText().isEmpty() || prenomField.getText().isEmpty() || emailField.getText().isEmpty() ||
                telephoneField.getText().isEmpty()) {
            Popup.showErrorMessage("Tous les champs sont requis.");
            return false;
        }

        if (!emailField.getText().matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            Popup.showErrorMessage("Email invalide.");
            return false;
        }

        return true;
    }

    private void clearFields() {
        nomField.clear();
        prenomField.clear();
        emailField.clear();
        telephoneField.clear();
    }
}
