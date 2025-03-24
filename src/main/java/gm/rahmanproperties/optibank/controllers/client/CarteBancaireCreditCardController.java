package gm.rahmanproperties.optibank.controllers.client;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import gm.rahmanproperties.optibank.domain.CarteBancaire;
import gm.rahmanproperties.optibank.domain.Client;
import gm.rahmanproperties.optibank.domain.Compte;
import gm.rahmanproperties.optibank.enums.StatutCarte;
import gm.rahmanproperties.optibank.service.CarteBancaireService;
import gm.rahmanproperties.optibank.service.UserSession;
import gm.rahmanproperties.optibank.utils.Popup;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;

import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.UUID;

public class CarteBancaireCreditCardController implements Initializable {
    @FXML
    private JFXTextField numeroCarteField, cvvField, dateExpirationField, soldeField, statutField, codePinField;
    @FXML
    private JFXButton demanderCarteButton, bloquerCarteButton, debloquerCarteButton;
    @FXML
    private Text compteNumero;
    @FXML
    private JFXTextField montantField, tauxField, dureeField, mensualiteField, tauxInteretField, statutCreditField;
    @FXML
    private StackPane stackPane;
    @FXML
    private Pane creditPane;

    private final CarteBancaireService carteService = new CarteBancaireService();
    private final Client currentUser = UserSession.getInstance().getLoggedInClient();
    private Compte currentCompte;

    boolean validateFieldSold() {
        return !soldeField.getText().trim().isEmpty();
    }

    public void setCompte(Compte compte) {
        this.currentCompte = compte;
        chargerDetailsCarte();
    }

    private void chargerDetailsCarte() {
        CarteBancaire carte = currentCompte.getCartes().get(0);
        if (carte != null) {
            numeroCarteField.setText(carte.getNumero());
            cvvField.setText(carte.getCvv());
            dateExpirationField.setText(carte.getDateExpiration().toString());
            soldeField.setText(carte.getSolde().toString());
            statutField.setText(carte.getStatut().toString());
            codePinField.setText(carte.getCodePin());
        }
    }

    @FXML
    public void demanderCredit() {

    }

    @FXML
    public void rembourserCredit() {

    }

    @FXML
    public void consulterEcheancier() {

    }

    @FXML
    public void demanderCarte() {
        if (!validateFieldSold()) {
            Popup.showErrorMessage("Veuillez saisir le solde!!!");
            Popup.showError("Veuillez saisir le solde!!!");
            return;
        }
        CarteBancaire nouvelleCarte = carteService.demanderCarte(currentCompte);
        Popup.showSuccess("Nouvelle carte générée : " + nouvelleCarte.getNumero());
        Popup.showSuccessMessage("Nouvelle carte générée : " + nouvelleCarte.getNumero());
        chargerDetailsCarte();
    }

    @FXML
    public void bloquerCarte() {
        CarteBancaire carte = currentCompte.getCartes().get(0);
        if (carte != null) {
            carteService.bloquerCarte(UUID.fromString(carte.getId()));
            statutField.setText(StatutCarte.BLOQUEE.name());
            Popup.showSuccess("Carte bloquée avec succès " + carte.getNumero());
            Popup.showSuccessMessage("Carte bloquée avec succès " + carte.getNumero());
        } else {
            Popup.showError("Aucune carte associée.");
            Popup.showErrorMessage("Aucune carte associée.");
        }
    }

    @FXML
    public void debloquerCarte() {
        CarteBancaire carte = currentCompte.getCartes().get(0);
        if (carte != null && Objects.equals(carte.getStatut(), StatutCarte.BLOQUEE)) {
            carte.setStatut(StatutCarte.ACTIVE);
            statutField.setText("ACTIVE");
            Popup.showSuccess("Carte débloquée avec succès. " + carte.getNumero());
            Popup.showSuccessMessage("Carte débloquée avec succès. " + carte.getNumero());
        } else {
            Popup.showError("La carte n'est pas bloquée ou n'existe pas.");
            Popup.showErrorMessage("La carte n'est pas bloquée ou n'existe pas.");
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        if (currentUser.getComptes() != null && !currentUser.getComptes().isEmpty()) {
            currentCompte = currentUser.getComptes().get(0);
        }
        numeroCarteField.setText(currentCompte.getNumero());
    }
}