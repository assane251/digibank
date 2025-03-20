package gm.rahmanproperties.digibank.controllers.client;

import com.itextpdf.text.Document;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDialog;
import com.jfoenix.controls.JFXDialogLayout;
import com.jfoenix.controls.JFXTextField;
import gm.rahmanproperties.digibank.domain.Client;
import gm.rahmanproperties.digibank.domain.Compte;
import gm.rahmanproperties.digibank.domain.Transaction;
import gm.rahmanproperties.digibank.enums.Type;
import gm.rahmanproperties.digibank.repository.TransactionRepository;
import gm.rahmanproperties.digibank.service.CompteService;
import gm.rahmanproperties.digibank.service.TransactionService;
import gm.rahmanproperties.digibank.service.UserSession;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import lombok.val;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileOutputStream;
import java.math.BigDecimal;
import java.net.URL;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.util.List;
import java.util.ResourceBundle;
import java.util.UUID;

public class ProfilClientController implements Initializable {

    @FXML private Pane infoClient;
    @FXML private Text profileNameClient;
    @FXML private Pane allInfo;
    @FXML private Text numero_compte_tf;
    @FXML private Text solde_tf;
    @FXML private Text status_tf;
    @FXML private StackPane stackPane;

    private final DecimalFormat df = new DecimalFormat("#,##0.00");
    CompteService compteService = new CompteService();
    TransactionRepository transactionRepository = new TransactionRepository();
    TransactionService transactionService = new TransactionService(transactionRepository, compteService); // À injecter
    private Compte currentCompte;
    Client currentUser = UserSession.getInstance().getLoggedInClient();

    @Override
    public void initialize(URL location, ResourceBundle resources) {


        if (currentUser == null) {
            infoClient.setVisible(false);
            profileNameClient.setText("Utilisateur non connecté");
            return;
        }

        profileNameClient.setText(currentUser.getNom() + " " + currentUser.getPrenom());

        if (currentUser.getComptes() != null && !currentUser.getComptes().isEmpty()) {
            currentCompte = currentUser.getComptes().get(0);
            infoClient.setVisible(true);
            numero_compte_tf.setText(currentCompte.getNumero());
            solde_tf.setText(df.format(currentCompte.getSolde()) + " CFA");
            status_tf.setText(currentUser.getStatus().toString());
        } else {
            infoClient.setVisible(false);
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

        courantBtn.setOnAction(e -> createCompte(Type.COURANT));
        epargneBtn.setOnAction(e -> createCompte(Type.EPARGNE));

        body.getChildren().addAll(info, courantBtn, epargneBtn);
        content.setBody(body);

        showDialog(content);
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
                    showError("Le montant doit être positif");
                    return;
                }
                handleDepot(montant);
            } catch (NumberFormatException ex) {
                showError("Montant invalide");
            }
        });

        body.getChildren().addAll(montantField, confirmBtn);
        content.setBody(body);
        showDialog(content);
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
            } catch (NumberFormatException ex) {
                showError("Montant invalide");
            }
        });

        body.getChildren().addAll(montantField, confirmBtn);
        content.setBody(body);
        showDialog(content);
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
                    showError("Le montant doit être positif");
                    return;
                }
                Compte compteDest = compteService.findByNumero(destNumero);
                if (compteDest == null) {
                    showError("Compte destinataire non trouvé");
                    return;
                }
                handleVirement(montant, compteDest.getId());
            } catch (NumberFormatException ex) {
                showError("Montant invalide");
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

    private void showSuccess(String message) {
        JFXDialogLayout content = new JFXDialogLayout();
        content.setHeading(new Text("Succès"));
        content.setBody(new Label(message));
        showDialog(content);
    }

    private void handleDepot(BigDecimal montant) {
        try {
            transactionService.createDepot(currentCompte.getId(), montant);
            updateUI();
            showSuccess("Dépôt effectué avec succès");
        } catch (Exception e) {
            showError("Erreur lors du dépôt: " + e.getMessage());
        }
    }

    private void handleRetrait(BigDecimal montant) {
        try {
            transactionService.createRetrait(currentCompte.getId(), montant);
            updateUI();
            showSuccess("Retrait effectué avec succès");
        } catch (Exception e) {
            showError("Erreur lors du retrait: " + e.getMessage());
        }
    }

    private void handleVirement(BigDecimal montant, UUID compteDestId) {
        try {
            transactionService.createVirement(currentCompte.getId(), compteDestId, montant);
            updateUI();
            showSuccess("Virement effectué avec succès");
        } catch (Exception e) {
            showError("Erreur lors du virement: " + e.getMessage());
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
            showSuccess("PDF exporté avec succès");
        } catch (Exception e) {
            showError("Erreur lors de l'export PDF: " + e.getMessage());
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
            showSuccess("Excel exporté avec succès");
        } catch (Exception e) {
            showError("Erreur lors de l'export Excel: " + e.getMessage());
        }
    }

    private void createCompte(Type type) {
        try {
            Compte compte = Compte.builder()
                    .numero("COMP" + UUID.randomUUID().toString().substring(0, 8))
                    .type(type)
                    .solde(BigDecimal.ZERO)
                    .dateCreation(LocalDate.now())
                    .client(UserSession.getInstance().getLoggedInClient())
                    .build();
            compteService.creerCompte(currentUser, type, BigDecimal.valueOf(Long.parseLong(solde_tf.getText())));
            currentCompte = compte;
            updateUI();
            showSuccess("Compte créé avec succès");
        } catch (Exception e) {
            showError("Erreur lors de la création du compte: " + e.getMessage());
        }
    }

    private void updateUI() {
        solde_tf.setText(df.format(currentCompte.getSolde()) + " CFA");
        numero_compte_tf.setText(currentCompte.getNumero());
        infoClient.setVisible(true);
    }
}