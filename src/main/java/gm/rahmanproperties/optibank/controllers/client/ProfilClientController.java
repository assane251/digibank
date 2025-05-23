package gm.rahmanproperties.optibank.controllers.client;

import com.itextpdf.text.Document;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDialog;
import com.jfoenix.controls.JFXDialogLayout;
import com.jfoenix.controls.JFXTextField;
import gm.rahmanproperties.optibank.domain.Client;
import gm.rahmanproperties.optibank.domain.Compte;
import gm.rahmanproperties.optibank.domain.Transaction;
import gm.rahmanproperties.optibank.enums.Type;
import gm.rahmanproperties.optibank.repository.TransactionRepository;
import gm.rahmanproperties.optibank.service.CarteBancaireService;
import gm.rahmanproperties.optibank.service.CompteService;
import gm.rahmanproperties.optibank.service.TransactionService;
import gm.rahmanproperties.optibank.service.UserSession;
import gm.rahmanproperties.optibank.utils.JavaMailer;
import gm.rahmanproperties.optibank.utils.Popup;
import gm.rahmanproperties.optibank.utils.WindowManager;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import javax.mail.MessagingException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.URL;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.util.List;
import java.util.ResourceBundle;
import java.util.UUID;

public class ProfilClientController implements Initializable {

    @FXML
    private Pane infoClient;
    @FXML
    private Text profileNameClient;
    @FXML
    private Pane allInfo;
    @FXML
    private Text numero_compte_tf;
    @FXML
    private Text solde_tf;
    @FXML
    private Text status_tf;
    @FXML
    private StackPane stackPane;
    @FXML
    private TableView<Transaction> transactionsTable;
    @FXML
    private TableColumn<Transaction, String> dateColumn;
    @FXML
    private TableColumn<Transaction, String> typeColumn;
    @FXML
    private TableColumn<Transaction, String> montantColumn;
    @FXML
    private TableColumn<Transaction, String> statusColumn;
    @FXML
    private Text cvv_tf;
    @FXML
    private Text numeroCarte_tf;
    @FXML
    private Text soldeCarte_tf;
    @FXML
    private Text dateExpiration_tf;
    @FXML
    private Text codePin_tf;
    @FXML
    private Text statutCarte_tf;
    @FXML
    private VBox carteBox;

    private final DecimalFormat df = new DecimalFormat("#,##0.00");
    private final CompteService compteService = new CompteService();
    private final TransactionRepository transactionRepository = new TransactionRepository();
    private final TransactionService transactionService = new TransactionService(transactionRepository, compteService);
    private Compte currentCompte;
    private final Client currentUser = UserSession.getInstance().getLoggedInClient();
    private final CarteBancaireService carteBancaireService = new CarteBancaireService();

    String email = System.getenv("email");
    String password = System.getenv("password");

    JavaMailer javaMailer = new JavaMailer(email, password);

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        if (currentUser == null) {
            infoClient.setVisible(false);
            profileNameClient.setText("Utilisateur non connecté");
            return;
        }

        profileNameClient.setText(currentUser.getNom() + " " + currentUser.getPrenom());
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("date"));
        typeColumn.setCellValueFactory(new PropertyValueFactory<>("type"));
        montantColumn.setCellValueFactory(new PropertyValueFactory<>("montant"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));

        if (currentUser.getComptes() != null && !currentUser.getComptes().isEmpty()) {
            currentCompte = currentUser.getComptes().get(0);
            infoClient.setVisible(true);
            numero_compte_tf.setText(currentCompte.getNumero());
            BigDecimal solde = currentCompte.getSolde();
            solde_tf.setText(solde != null ? df.format(solde) + " CFA" : "0.00 CFA");
            status_tf.setText(currentUser.getStatus().toString());
            loadClientInfo();
        } else {
            infoClient.setVisible(false);
        }

        if (currentCompte.getCartes() != null && !currentCompte.getCartes().isEmpty()) {
            carteBox.setVisible(true);
            cvv_tf.setText(currentCompte.getCartes().get(0).getCvv());
            numeroCarte_tf.setText(currentCompte.getCartes().get(0).getNumero());
            soldeCarte_tf.setText(String.valueOf(currentCompte.getCartes().get(0).getSolde()));
            dateExpiration_tf.setText(currentCompte.getCartes().get(0).getDateExpiration().toString());
//            codePin_tf.setText(currentCompte.getCartes().get(0).getCodePin());
            statutCarte_tf.setText(currentCompte.getCartes().get(0).getStatut().name());
        } else {
            carteBox.setVisible(false);
        }
    }

    private void loadClientInfo() {
        loadTransactions();
    }

    private void loadTransactions() {
        List<Transaction> transactions = transactionService.getTransactionsByCompte(currentCompte.getId());
        if (transactions.isEmpty()) {
            Popup.showSuccessMessage("Aucune transaction effectuée");
        } else {
            transactionsTable.getItems().setAll(transactions);
            transactionsTable.setVisible(true);
        }
    }



    @FXML
    void btnGestionTransaction(ActionEvent event) {
        JFXDialogLayout content = new JFXDialogLayout();
        content.setHeading(new Text("Gestion des Transactions"));

        VBox body = new VBox(15);
        body.setPadding(new Insets(15));

        JFXButton depotBtn = createStyledButton("Nouveau Dépôt");
        JFXButton retraitBtn = createStyledButton("Nouveau Retrait");
        JFXButton virementBtn = createStyledButton("Nouveau Virement");

        depotBtn.setOnAction(e -> showDepotDialog());
        retraitBtn.setOnAction(e -> showRetraitDialog());
        virementBtn.setOnAction(e -> showVirementDialog());

        body.getChildren().addAll(depotBtn, retraitBtn, virementBtn);
        content.setBody(body);
        //reloadPage();

        showDialog(content);
    }

    @FXML
    void btnHistoriqueFinancier(ActionEvent event) {
        JFXDialogLayout content = new JFXDialogLayout();
        content.setHeading(new Text("Historique Financier"));

        VBox body = new VBox(15);
        body.setPadding(new Insets(15));

        List<Transaction> transactions = transactionService.getTransactionsByCompte(currentCompte.getId());
        if (transactions.isEmpty()) {
            Label noTrans = new Label("Aucune transaction effectuée");
            noTrans.setStyle("-fx-font-size: 16px; -fx-text-fill: #666666;");
            body.getChildren().add(noTrans);
        } else {
            Label info = new Label("Consultation de l'historique des transactions");
            JFXButton exportPdf = createStyledButton("Exporter en PDF");
            JFXButton exportExcel = createStyledButton("Exporter en Excel");

            exportPdf.setOnAction(e -> exportToPdf(transactions));
            exportExcel.setOnAction(e -> exportToExcel(transactions));

            body.getChildren().addAll(info, exportPdf, exportExcel);
        }
        content.setBody(body);
        showDialog(content);
        //reloadPage();
    }

    @FXML
    void btnOuvertureCompte(ActionEvent event) {
        JFXDialogLayout content = new JFXDialogLayout();
        content.setHeading(new Text("Ouverture de Compte"));

        VBox body = new VBox(15);
        body.setPadding(new Insets(15));

        Label info = new Label("Créer un nouveau compte bancaire");
        JFXButton courantBtn = createStyledButton("Compte Courant");
        JFXButton epargneBtn = createStyledButton("Compte Épargne");
        JFXTextField txtSolde = new JFXTextField();
        txtSolde.setPromptText("Solde");

        // Pass the txtSolde field directly to createCompte
        courantBtn.setOnAction(e -> createCompte(Type.COURANT, txtSolde));
        epargneBtn.setOnAction(e -> createCompte(Type.EPARGNE, txtSolde));

        body.getChildren().addAll(info, courantBtn, epargneBtn, txtSolde);
        content.setBody(body);

        showDialog(content);
        // reloadPage();
    }

    private void showDepotDialog() {
        JFXDialogLayout content = new JFXDialogLayout();
        content.setHeading(new Text("Nouveau Dépôt"));

        VBox body = new VBox(15);
        body.setPadding(new Insets(15));

        JFXTextField montantField = new JFXTextField();
        montantField.setPromptText("Montant (CFA)");
        JFXButton confirmBtn = createStyledButton("Confirmer");

        confirmBtn.setOnAction(e -> {
            try {
                BigDecimal montant = new BigDecimal(montantField.getText());
                if (montant.compareTo(BigDecimal.ZERO) <= 0) {
                    Popup.showErrorMessage("Le montant doit être positif");
                    return;
                }
                handleDepot(montant);
                javaMailer.sendEmail(currentUser.getEmail(), "Dépôt", "Vous avez effectué un dépôt de " + montant + " CFA");
            } catch (NumberFormatException ex) {
                showError("Montant invalide");
                Popup.showErrorMessage("Montant invalide");
            } catch (MessagingException ex) {
                throw new RuntimeException(ex);
            }
        });

        body.getChildren().addAll(montantField, confirmBtn);
        content.setBody(body);
        showDialog(content);
        //reloadPage();
    }

    private void showRetraitDialog() {
        JFXDialogLayout content = new JFXDialogLayout();
        content.setHeading(new Text("Nouveau Retrait"));

        VBox body = new VBox(15);
        body.setPadding(new Insets(15));

        JFXTextField montantField = new JFXTextField();
        montantField.setPromptText("Montant (CFA)");
        JFXButton confirmBtn = createStyledButton("Confirmer");

        confirmBtn.setOnAction(e -> {
            try {
                BigDecimal montant = new BigDecimal(montantField.getText());
                if (montant.compareTo(BigDecimal.ZERO) <= 0) {
                    showError("Le montant doit être positif");
                    return;
                }
                handleRetrait(montant);
                javaMailer.sendEmail(currentUser.getEmail(), "Retrait", "Vous avez effectué un retrait de " + montant + " CFA");
            } catch (NumberFormatException ex) {
                Popup.showErrorMessage("Montant invalide");
                showError("Montant invalide");
            } catch (MessagingException ex) {
                throw new RuntimeException(ex);
            }
        });

        body.getChildren().addAll(montantField, confirmBtn);
        content.setBody(body);
        showDialog(content);
        //reloadPage();
    }

    private void showVirementDialog() {
        JFXDialogLayout content = new JFXDialogLayout();
        content.setHeading(new Text("Nouveau Virement"));

        VBox body = new VBox(15);
        body.setPadding(new Insets(15));

        JFXTextField montantField = new JFXTextField();
        montantField.setPromptText("Montant (CFA)");
        JFXTextField destField = new JFXTextField();
        destField.setPromptText("Numéro du compte destinataire");
        JFXButton confirmBtn = createStyledButton("Confirmer");

        confirmBtn.setOnAction(e -> {
            try {
                BigDecimal montant = new BigDecimal(montantField.getText());
                String destNumero = destField.getText();
                if (montant.compareTo(BigDecimal.ZERO) <= 0) {
                    Popup.showErrorMessage("Montant invalide");
                    showError("Le montant doit être positif");
                    return;
                }
                Compte compteDest = compteService.findByNumero(destNumero);
                if (compteDest == null) {
                    Popup.showErrorMessage("Compte destinataire non trouvé");
                    showError("Compte destinataire non trouvé");
                    return;
                }
                handleVirement(montant, compteDest.getId());
                javaMailer.sendEmail(currentUser.getEmail(), "Virement", "Vous avez effectué un virement de " + montant + " CFA");
            } catch (NumberFormatException ex) {
                showError("Montant invalide");
            } catch (MessagingException ex) {
                throw new RuntimeException(ex);
            }
        });

        body.getChildren().addAll(montantField, destField, confirmBtn);
        content.setBody(body);
        showDialog(content);
    }

    private JFXButton createStyledButton(String text) {
        JFXButton button = new JFXButton(text);
        button.setStyle(
                "-fx-background-color: #4682B4;" +
                        "-fx-text-fill: white;" +
                        "-fx-font-size: 14px;" +
                        "-fx-pref-width: 200px;" +
                        "-fx-pref-height: 40px;" +
                        "-fx-background-radius: 5px;"
        );
        return button;
    }

    private void showDialog(JFXDialogLayout content) {
        JFXDialog dialog = new JFXDialog(stackPane, content, JFXDialog.DialogTransition.CENTER);
        JFXButton closeButton = new JFXButton("Fermer");
        closeButton.setStyle(
                "-fx-background-color: #dc3545;" +
                        "-fx-text-fill: white;" +
                        "-fx-font-size: 14px;"
        );
        closeButton.setOnAction(e -> dialog.close());
        content.setActions(closeButton);
        Platform.runLater(dialog::show);
    }

    private void showError(String message) {
        JFXDialogLayout content = new JFXDialogLayout();
        content.setHeading(new Text("Erreur"));
        content.setBody(new Label(message));
        showDialog(content);
    }

    private void handleDepot(BigDecimal montant) {
        try {
            transactionService.createDepot(currentCompte.getId(), montant);
            updateUI();
            Popup.showSuccessMessage("Dépôt effectué avec succès");
        } catch (Exception e) {
            Popup.showErrorMessage("Erreur lors du dépôt: " + e.getMessage());
        }
    }

    private void handleRetrait(BigDecimal montant) {
        try {
            transactionService.createRetrait(currentCompte.getId(), montant);
            updateUI();
            Popup.showSuccessMessage("Retrait effectué avec succès");
        } catch (Exception e) {
            Popup.showErrorMessage("Erreur lors du retrait: " + e.getMessage());
        }
    }

    private void handleVirement(BigDecimal montant, UUID compteDestId) {
        try {
            transactionService.createVirement(currentCompte.getId(), compteDestId, montant);
            updateUI();
            Popup.showSuccessMessage("Virement effectué avec succès");
        } catch (Exception e) {
            Popup.showErrorMessage("Erreur lors du virement: " + e.getMessage());
        }
    }

    private void exportToPdf(List<Transaction> transactions) {
        try {
            Document document = new Document();
            PdfWriter.getInstance(document, new FileOutputStream("historique.pdf"));
            document.open();
            document.add(new Paragraph("Historique des Transactions"));
            for (Transaction t : transactions) {
                document.add(new Paragraph(
                        String.format("%s - %s - %s CFA - %s",
                                t.getDate(), t.getType(), df.format(t.getMontant()), t.getStatus())));
            }
            document.close();
            Popup.showSuccessMessage("PDF exporté avec succès");
        } catch (Exception e) {
            Popup.showErrorMessage("Erreur lors de l'export PDF: " + e.getMessage());
        }
    }

    private void exportToExcel(List<Transaction> transactions) {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Historique");
            Row header = sheet.createRow(0);
            header.createCell(0).setCellValue("Date");
            header.createCell(1).setCellValue("Type");
            header.createCell(2).setCellValue("Montant");
            header.createCell(3).setCellValue("Statut");

            int rowNum = 1;
            for (Transaction t : transactions) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(t.getDate().toString());
                row.createCell(1).setCellValue(t.getType().toString());
                row.createCell(2).setCellValue(t.getMontant().doubleValue());
                row.createCell(3).setCellValue(t.getStatus().toString());
            }

            try (FileOutputStream fileOut = new FileOutputStream("historique.xlsx")) {
                workbook.write(fileOut);
            }
            Popup.showSuccessMessage("Excel exporté avec succès");
        } catch (Exception e) {
            Popup.showErrorMessage("Erreur lors de l'export Excel: " + e.getMessage());
        }
    }

    // Updated createCompte to accept txtSolde as a parameter
    private void createCompte(Type type, JFXTextField txtSolde) {
        try {
            BigDecimal solde;
            String soldeInput = txtSolde.getText().trim();
            if (soldeInput.isEmpty()) {
                Popup.showErrorMessage("Veuillez entrer un solde initial.");
                return;
            }
            try {
                solde = new BigDecimal(soldeInput);
                if (solde.compareTo(BigDecimal.ZERO) < 0) {
                    Popup.showErrorMessage("Le solde ne peut pas être négatif.");
                    return;
                }
            } catch (NumberFormatException e) {
                Popup.showErrorMessage("Montant invalide ! Veuillez entrer un nombre valide.");
                return;
            }

            Compte compte = Compte.builder()
                    .numero("COMP" + UUID.randomUUID().toString().substring(0, 8))
                    .type(type)
                    .solde(solde)
                    .dateCreation(LocalDate.now())
                    .client(UserSession.getInstance().getLoggedInClient())
                    .build();

            compteService.creerCompte(currentUser, type, solde);
            currentCompte = compte;
            updateUI();
            Popup.showSuccessMessage("Compte créé avec succès");
        } catch (Exception e) {
            Popup.showErrorMessage("Erreur lors de la création du compte: " + e.getMessage());
        }
    }

    private void updateUI() {
        BigDecimal solde = currentCompte.getSolde();
        solde_tf.setText(solde != null ? df.format(solde) + " CFA" : "0.00 CFA");
        numero_compte_tf.setText(currentCompte.getNumero());
        infoClient.setVisible(true);
    }

    public void btnDemandeCarte(ActionEvent event) {
        WindowManager.openParentAndChild("/fxml/client/credit_carte.fxml", "Carte Bancaire");
    }
}