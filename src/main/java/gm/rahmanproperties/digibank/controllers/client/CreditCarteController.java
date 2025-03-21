package gm.rahmanproperties.digibank.controllers.client;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXListView;
import com.jfoenix.controls.JFXTextField;
import gm.rahmanproperties.digibank.domain.Client;
import gm.rahmanproperties.digibank.domain.Credit;
import gm.rahmanproperties.digibank.service.CreditService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.text.Text;
import lombok.Setter;

public class CreditCarteController {
    @FXML
    private JFXTextField montantField, tauxField, dureeField, mensualiteField, statutCreditField;
    @FXML Text compteNumero;
    @FXML
    private JFXListView<String> echeancierList;
    @FXML
    private JFXButton simulerButton, demanderButton;
    @FXML
    private Text messageText;

//    private CreditService creditService = new CreditService();
    @Setter
    private Client client;
    private Credit creditActuel;

    @FXML
    public void handleSimulerCredit() {
        try {
            double montant = Double.parseDouble(montantField.getText());
            double taux = Double.parseDouble(tauxField.getText());
            int duree = Integer.parseInt(dureeField.getText());

//            double mensualite = creditService.calculerMensualite(montant, taux, duree);
//            mensualiteField.setText(String.format("%.2f €", mensualite));
//
//            // Simulation de l'échéancier
//            echeancierList.getItems().clear();
//            double soldeRestant = montant;
//            for (int i = 1; i <= duree; i++) {
//                soldeRestant -= (mensualite - (soldeRestant * taux / 100 / 12));
//                echeancierList.getItems().add("Mois " + i + " : Mensualité = " + String.format("%.2f €", mensualite) + ", Solde restant = " + String.format("%.2f €", soldeRestant));
//            }
            messageText.setText("Simulation effectuée.");
        } catch (NumberFormatException e) {
            messageText.setText("Veuillez entrer des valeurs valides.");
        }
    }

    @FXML
    public void handleDemanderCredit() {
        try {
            double montant = Double.parseDouble(montantField.getText());
            double taux = Double.parseDouble(tauxField.getText());
            int duree = Integer.parseInt(dureeField.getText());

//            creditActuel = creditService.demanderCredit(client, montant, taux, duree);
//            statutCreditField.setText(creditActuel.getStatut());
            messageText.setText("Demande de crédit envoyée. En attente de validation.");
        } catch (NumberFormatException e) {
            messageText.setText("Veuillez entrer des valeurs valides.");
        }
    }

    public void demanderCredit(ActionEvent event) {
    }

    public void rembourserCredit(ActionEvent event) {
    }

    public void consulterEcheancier(ActionEvent event) {
    }

    public void demanderCarte(ActionEvent event) {
    }

    public void bloquerCarte(ActionEvent event) {
    }

    public void debloquerCarte(ActionEvent event) {
    }
}
