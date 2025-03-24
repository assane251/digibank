package gm.rahmanproperties.optibank.controllers.client;

import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTextField;
import gm.rahmanproperties.optibank.domain.Client;
import gm.rahmanproperties.optibank.dtos.ClientDto;
import gm.rahmanproperties.optibank.enums.Status;
import gm.rahmanproperties.optibank.enums.Type;
import gm.rahmanproperties.optibank.service.ClientService;
import gm.rahmanproperties.optibank.utils.JavaMailer;
import gm.rahmanproperties.optibank.utils.Popup;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.text.Text;

import java.math.BigDecimal;
import java.net.URL;
import java.time.LocalDate;
import java.util.ResourceBundle;
import javafx.application.Platform;

public class CreateAccountController implements Initializable {

    // Ajouter Client Tab
    @FXML private JFXTextField nomField;
    @FXML private JFXTextField prenomField;
    @FXML private JFXTextField emailField;
    @FXML private JFXTextField telephoneField;
    @FXML private JFXComboBox<Type> typeCompteComboBox;
    @FXML private JFXTextField soldeField;

    // Lister Client Tab
    @FXML private TableView<Client> clientTable;
    @FXML private TableColumn<Client, String> nomColumn;
    @FXML private TableColumn<Client, String> prenomColumn;
    @FXML private TableColumn<Client, String> emailColumn;
    @FXML private TableColumn<Client, String> telephoneColumn;
    @FXML private TableColumn<Client, Status> statusColumn;

    // Active/Desactive Compte Tab
    @FXML private JFXComboBox<Client> clientComboBox;
    @FXML private Text currentStatusText;
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
                    .password("passer123")
                    .dateInscription(LocalDate.now())
                    .firstLogin(true)
                    .build();

            Type typeCompte = typeCompteComboBox.getValue();
            BigDecimal soldeInitial = new BigDecimal(soldeField.getText());

            Client client = clientService.creerClient(clientDto, typeCompte, soldeInitial);
            Popup.showSuccessMessage("Client créé avec succès!");
            javaMailer.sendEmail(email, client.getEmail(), "Bienvenue chez DigiBank" + "Votre compte a été créé avec succès.\nVotre email"
                                                                        + client.getEmail() + "\nmot de passe par defaut: " + "<strong>passer123</strong>");
            clearFields();
            refreshComboBox();
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

        try {
            new BigDecimal(soldeField.getText());
        } catch (NumberFormatException e) {
            Popup.showErrorMessage("Le solde initial doit être un nombre valide (ex: 1000.00).");
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

    public void handleActivate(ActionEvent event) {
        Client selectedClient = clientComboBox.getSelectionModel().getSelectedItem();
        if (selectedClient == null) {
            Popup.showErrorMessage("Veuillez sélectionner un client.");
            return;
        }
        try {
            clientService.toggleClientStatus(selectedClient.getEmail(), Status.ACTIF);
            Popup.showSuccessMessage("Compte activé!");
            javaMailer.sendEmail("Compte activé", "Votre compte a été activé avec succès.", selectedClient.getEmail());
            updateStatusText(selectedClient);
            refreshClientTable();
        } catch (Exception e) {
            Popup.showErrorMessage("Erreur" + e.getMessage());
        }
    }

    public void handleDeactivate(ActionEvent event) {
        Client selectedClient = clientComboBox.getSelectionModel().getSelectedItem();
        if (selectedClient == null) {
            Popup.showErrorMessage("Veuillez sélectionner un client.");
            return;
        }
        try {
            clientService.toggleClientStatus(selectedClient.getEmail(), Status.EN_ATTENTE);
            Popup.showSuccessMessage("Compte désactivé!");
            updateStatusText(selectedClient);
            refreshClientTable();
        } catch (Exception e) {
            Popup.showErrorMessage("Erreur" + e.getMessage());
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setupClientTable();
        typeCompteComboBox.setItems(FXCollections.observableArrayList(Type.values()));
        // Initialize Active/Desactive Compte Tab
        setupToggleTab();
    }

// Setup for Lister Client Tab
private void setupClientTable() {
    nomColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getNom()));
    prenomColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getPrenom()));
    emailColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getEmail()));
    telephoneColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getTelephone()));
    statusColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleObjectProperty<>(cellData.getValue().getStatus()));
    refreshClientTable();
}

// Setup for Active/Desactive Compte Tab
private void setupToggleTab() {
    refreshComboBox();
    clientComboBox.getSelectionModel().selectedItemProperty().addListener((obs, oldValue, newValue) -> {
        if (newValue != null) {
            updateStatusText(newValue);
        } else {
            currentStatusText.setText("Statut actuel: N/A");
        }
    });
}

// Refresh TableView data
    private void refreshClientTable() {
        Platform.runLater(() -> clientTable.setItems(FXCollections.observableArrayList(clientService.listerClients())));
    }

    // Refresh ComboBox data
    private void refreshComboBox() {
        Platform.runLater(() -> {
            clientComboBox.setItems(FXCollections.observableArrayList(clientService.listerClients()));
            clientComboBox.setCellFactory(param -> new ListCell<>() {
                @Override
                protected void updateItem(Client item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setText(null);
                    } else {
                        setText(item.getEmail());
                    }
                }
            });
            clientComboBox.setButtonCell(new ListCell<>() {
                @Override
                protected void updateItem(Client item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setText("Sélectionner un client");
                    } else {
                        setText(item.getEmail());
                    }
                }
            });
        });
    }

// Update status text based on selected client
private void updateStatusText(Client client) {
    Client refreshedClient = clientService.authentificationClient(client.getEmail());
    if (refreshedClient != null) {
        currentStatusText.setText("Statut actuel: " + refreshedClient.getStatus().toString());
    }
}
}