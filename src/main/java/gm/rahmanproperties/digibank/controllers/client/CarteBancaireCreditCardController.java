package gm.rahmanproperties.digibank.controllers.client;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import gm.rahmanproperties.digibank.domain.CarteBancaire;
import gm.rahmanproperties.digibank.domain.Compte;
import gm.rahmanproperties.digibank.enums.StatutCarte;
import gm.rahmanproperties.digibank.service.CarteBancaireService;
import javafx.fxml.FXML;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;

import java.util.Objects;
import java.util.UUID;

public class CarteBancaireCreditCardController {
    @FXML
    private JFXTextField numeroCarteField, cvvField, dateExpirationField, soldeField, statutField, codePinField;
    @FXML
    private JFXButton demanderCarteButton, bloquerCarteButton, debloquerCarteButton;
    @FXML
    private Text messageText;
    @FXML
    private Text compteNumero;
    @FXML
    private JFXTextField montantField, tauxField, dureeField, mensualiteField, tauxInteretField, statutCreditField;
    @FXML
    private StackPane stackPane;
    @FXML
    private Pane creditPane;

    private final CarteBancaireService carteService = new CarteBancaireService();
    private Compte compte;

    public void setCompte(Compte compte) {
        this.compte = compte;
        chargerDetailsCarte();
    }

    private void chargerDetailsCarte() {
        CarteBancaire carte = compte.getCartes().get(0);
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
        CarteBancaire nouvelleCarte = carteService.demanderCarte(compte);
        messageText.setText("Nouvelle carte générée : " + nouvelleCarte.getNumero());
        chargerDetailsCarte();
    }

    @FXML
    public void bloquerCarte() {
        CarteBancaire carte = compte.getCartes().get(0);
        if (carte != null) {
            carteService.bloquerCarte(UUID.fromString(carte.getId()));
            statutField.setText("BLOQUÉE");
            messageText.setText("Carte bloquée avec succès.");
        } else {
            messageText.setText("Aucune carte associée.");
        }
    }

    @FXML
    public void debloquerCarte() {
        CarteBancaire carte = compte.getCartes().get(0);
        if (carte != null && Objects.equals(carte.getStatut(), StatutCarte.BLOQUEE)) {
            carte.setStatut(StatutCarte.ACTIVE);
            statutField.setText("ACTIVE");
            messageText.setText("Carte débloquée avec succès.");
        } else {
            messageText.setText("La carte n'est pas bloquée ou n'existe pas.");
        }
    }
}