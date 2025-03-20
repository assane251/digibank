//package gm.rahmanproperties.digibank.controllers.client;
//
//import com.jfoenix.controls.JFXButton;
//import com.jfoenix.controls.JFXDialog;
//import com.jfoenix.controls.JFXDialogLayout;
//import com.jfoenix.controls.JFXTextField;
//import gm.rahmanproperties.digibank.domain.CarteBancaire;
//import gm.rahmanproperties.digibank.domain.Client;
//import gm.rahmanproperties.digibank.domain.Compte;
//import gm.rahmanproperties.digibank.domain.Credit;
//import gm.rahmanproperties.digibank.domain.Remboursement;
//import gm.rahmanproperties.digibank.service.CarteBancaireService;
//import gm.rahmanproperties.digibank.service.CompteService;
//import gm.rahmanproperties.digibank.service.CreditService;
//import gm.rahmanproperties.digibank.service.UserSession;
//import javafx.application.Platform;
//import javafx.event.ActionEvent;
//import javafx.fxml.FXML;
//import javafx.fxml.Initializable;
//import javafx.geometry.Insets;
//import javafx.scene.control.Label;
//import javafx.scene.layout.Pane;
//import javafx.scene.layout.StackPane;
//import javafx.scene.layout.VBox;
//import javafx.scene.text.Text;
//
//import java.math.BigDecimal;
//import java.net.URL;
//import java.text.DecimalFormat;
//import java.util.List;
//import java.util.ResourceBundle;
//
//public class CreditCarteController implements Initializable {
//
//    @FXML private StackPane stackPane;
//    @FXML private Text compteNumero;
//    @FXML private Pane creditPane;
//    @FXML private Pane cartePane;
//
//    private CreditService creditService; // À injecter
//    private CarteBancaireService carteService; // À injecter
//    private CompteService compteService; // À injecter
//    private Compte currentCompte;
//    private final DecimalFormat df = new DecimalFormat("#,##0.00");
//
//    @Override
//    public void initialize(URL location, ResourceBundle resources) {
//        Client currentUser = UserSession.getInstance().getLoggedInClient();
//        if (currentUser != null && !currentUser.getComptes().isEmpty()) {
//            currentCompte = currentUser.getComptes().get(0);
//            compteNumero.setText(currentCompte.getNumero());
//            updateCreditPane();
//            updateCartePane();
//        }
//    }
//
//    @FXML
//    void demanderCredit(ActionEvent event) {
//        JFXDialogLayout content = new JFXDialogLayout();
//        content.setHeading(new Text("Demande de Crédit"));
//
//        VBox body = new VBox(15);
//        body.setPadding(new Insets(15));
//
//        JFXTextField montantField = new JFXTextField();
//        montantField.setPromptText("Montant (CFA)");
//        JFXTextField tauxField = new JFXTextField();
//        tauxField.setPromptText("Taux d'intérêt (%)");
//        JFXTextField dureeField = new JFXTextField();
//        dureeField.setPromptText("Durée (mois)");
//        JFXButton confirmBtn = createStyledButton("Confirmer");
//
//        confirmBtn.setOnAction(e -> {
//            try {
//                BigDecimal montant = new BigDecimal(montantField.getText());
//                double taux = Double.parseDouble(tauxField.getText());
//                int duree = Integer.parseInt(dureeField.getText());
//                if (montant.compareTo(BigDecimal.ZERO) <= 0 || taux <= 0 || duree <= 0) {
//                    showError("Valeurs invalides");
//                    return;
//                }
//                creditService.demanderCredit(UserSession.getInstance().getLoggedInClient(), montant, taux, duree);
//                updateCreditPane();
//                showSuccess("Demande de crédit enregistrée");
//            } catch (Exception ex) {
//                showError("Erreur: " + ex.getMessage());
//            }
//        });
//
//        body.getChildren().addAll(montantField, tauxField, dureeField, confirmBtn);
//        content.setBody(body);
//        showDialog(content);
//    }
//
//    @FXML
//    void rembourserCredit(ActionEvent event) {
//        List<Credit> credits = creditService.getCreditsByClient(UserSession.getInstance().getLoggedInClient().getId());
//        if (credits.isEmpty()) {
//            showError("Aucun crédit à rembourser");
//            return;
//        }
//
//        JFXDialogLayout content = new JFXDialogLayout();
//        content.setHeading(new Text("Remboursement de Crédit"));
//
//        VBox body = new VBox(15);
//        body.setPadding(new Insets(15));
//
//        JFXTextField montantField = new JFXTextField();
//        montantField.setPromptText("Montant à rembourser (CFA)");
//        JFXButton confirmBtn = createStyledButton("Confirmer");
//
//        confirmBtn.setOnAction(e -> {
//            try {
//                BigDecimal montant = new BigDecimal(montantField.getText());
//                if (montant.compareTo(BigDecimal.ZERO) <= 0) {
//                    showError("Montant invalide");
//                    return;
//                }
//                Credit credit = credits.get(0); // Premier crédit approuvé
//                if (!"APPROUVE".equals(credit.getStatut())) {
//                    showError("Aucun crédit approuvé");
//                    return;
//                }
//                creditService.effectuerRemboursement(credit.getId(), montant);
//                updateCreditPane();
//                showSuccess("Remboursement effectué");
//            } catch (Exception ex) {
//                showError("Erreur: " + ex.getMessage());
//            }
//        });
//
//        body.getChildren().addAll(montantField, confirmBtn);
//        content.setBody(body);
//        showDialog(content);
//    }
//
//    @FXML
//    void consulterEcheancier(ActionEvent event) {
//        List<Credit> credits = creditService.getCreditsByClient(UserSession.getInstance().getLoggedInClient().getId());
//        if (credits.isEmpty()) {
//            showError("Aucun crédit trouvé");
//            return;
//        }
//
//        JFXDialogLayout content = new JFXDialogLayout();
//        content.setHeading(new Text("Échéancier de Remboursement"));
//
//        VBox body = new VBox(15);
//        body.setPadding(new Insets(15));
//
//        Credit credit = credits.get(0); // Premier crédit
//        List<Remboursement> remboursements = creditService.getEcheancier(credit.getId());
//        if (remboursements.isEmpty()) {
//            body.getChildren().add(new Label("Aucun remboursement effectué"));
//        } else {
//            for (Remboursement r : remboursements) {
//                body.getChildren().add(new Label(
//                        String.format("Date: %s - Montant: %s CFA", r.getDate(), df.format(r.getMontant()))));
//            }
//        }
//        content.setBody(body);
//        showDialog(content);
//    }
//
//    @FXML
//    void demanderCarte(ActionEvent event) {
//        carteService.demanderCarte(currentCompte);
//        updateCartePane();
//        showSuccess("Carte demandée avec succès");
//    }
//
//    @FXML
//    void bloquerCarte(ActionEvent event) {
//        List<CarteBancaire> cartes = carteService.getCartesByCompte(currentCompte.getId());
//        if (cartes.isEmpty()) {
//            showError("Aucune carte à bloquer");
//            return;
//        }
//        carteService.bloquerCarte(cartes.get(0).getId());
//        updateCartePane();
//        showSuccess("Carte bloquée");
//    }
//
//    @FXML
//    void debloquerCarte(ActionEvent event) {
//        List<CarteBancaire> cartes = carteService.getCartesByCompte(currentCompte.getId());
//        if (cartes.isEmpty()) {
//            showError("Aucune carte à débloquer");
//            return;
//        }
//        carteService.debloquerCarte(cartes.get(0).getId());
//        updateCartePane();
//        showSuccess("Carte débloquée");
//    }
//
//    private void updateCreditPane() {
//        List<Credit> credits = creditService.getCreditsByClient(UserSession.getInstance().getLoggedInClient().getId());
//        creditPane.getChildren().clear();
//        if (credits.isEmpty()) {
//            creditPane.getChildren().add(new Label("Aucun crédit en cours"));
//        } else {
//            for (Credit c : credits) {
//                VBox creditInfo = new VBox(5);
//                creditInfo.getChildren().addAll(
//                        new Label("Montant: " + df.format(c.getMontant()) + " CFA"),
//                        new Label("Mensualité: " + df.format(c.getMensualite()) + " CFA"),
//                        new Label("Statut: " + c.getStatut()));
//                creditPane.getChildren().add(creditInfo);
//            }
//        }
//    }
//
//    private void updateCartePane() {
//        List<CarteBancaire> cartes = carteService.getCartesByCompte(currentCompte.getId());
//        cartePane.getChildren().clear();
//        if (cartes.isEmpty()) {
//            cartePane.getChildren().add(new Label("Aucune carte associée"));
//        } else {
//            for (CarteBancaire c : cartes) {
//                VBox carteInfo = new VBox(5);
//                carteInfo.getChildren().addAll(
//                        new Label("Numéro: " + c.getNumero()),
//                        new Label("Statut: " + c.getStatut()));
//                cartePane.getChildren().add(carteInfo);
//            }
//        }
//    }
//
//    private JFXButton createStyledButton(String text) {
//        JFXButton button = new JFXButton(text);
//        button.setStyle(
//                "-fx-background-color: #4682B4;" +
//                        "-fx-text-fill: white;" +
//                        "-fx-font-size: 14px;" +
//                        "-fx-pref-width: 200px;" +
//                        "-fx-pref-height: 40px;" +
//                        "-fx-background-radius: 5px;"
//        );
//        return button;
//    }
//
//    private void showDialog(JFXDialogLayout content) {
//        JFXDialog dialog = new JFXDialog(stackPane, content, JFXDialog.DialogTransition.CENTER);
//        JFXButton closeButton = new JFXButton("Fermer");
//        closeButton.setStyle("-fx-background-color: #dc3545; -fx-text-fill: white; -fx-font-size: 14px;");
//        closeButton.setOnAction(e -> dialog.close());
//        content.setActions(closeButton);
//        Platform.runLater(dialog::show);
//    }
//
//    private void showError(String message) {
//        JFXDialogLayout content = new JFXDialogLayout();
//        content.setHeading(new Text("Erreur"));
//        content.setBody(new Label(message));
//        showDialog(content);
//    }
//
//    private void showSuccess(String message) {
//        JFXDialogLayout content = new JFXDialogLayout();
//        content.setHeading(new Text("Succès"));
//        content.setBody(new Label(message));
//        showDialog(content);
//    }
//}