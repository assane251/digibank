package gm.rahmanproperties.digibank.controllers.admin;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDialog;
import com.jfoenix.controls.JFXDialogLayout;
import gm.rahmanproperties.digibank.domain.Client;
import gm.rahmanproperties.digibank.enums.Status;
import gm.rahmanproperties.digibank.enums.Type;
import gm.rahmanproperties.digibank.service.ClientService;
import gm.rahmanproperties.digibank.service.CompteService;
import gm.rahmanproperties.digibank.utils.Popup;
import gm.rahmanproperties.digibank.utils.WindowManager;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.fx.ChartViewer;
import org.jfree.data.category.DefaultCategoryDataset;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Logger;

public class DashboardAdminController implements Initializable {

    @FXML private TableView<Client> clientTable;
    @FXML private TableColumn<Client, String> nomColumn;
    @FXML private TableColumn<Client, String> prenomColumn;
    @FXML private TableColumn<Client, String> emailColumn;
    @FXML private TableColumn<Client, Status> statusColumn;
    @FXML private TableColumn<Client, HBox> actionColumn;
    @FXML private Pane chartPane;
    @FXML private JFXButton detailsClientsBtn;
    @FXML private JFXButton detailsComptesBtn;
    @FXML private JFXButton detailsTransactionsBtn;
    @FXML private JFXButton detailsTicketsBtn;
    @FXML private GridPane contentGrid;

    private final ObservableList<Client> clientData = FXCollections.observableArrayList();
    private StackPane dialogRoot;

    private final ClientService clientService = new ClientService();
    private final CompteService compteService = new CompteService();
    private static final Logger log = Logger.getLogger(DashboardAdminController.class.getName());

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        dialogRoot = new StackPane();
        contentGrid.getChildren().add(dialogRoot);

        setupTableView();
        loadData();
        setupChart();

        detailsClientsBtn.setOnAction(this::showClientDetails);
        detailsComptesBtn.setOnAction(this::showCompteDetails);
        detailsTransactionsBtn.setOnAction(this::showTransactionDetails);
        detailsTicketsBtn.setOnAction(this::showTicketDetails);
    }

    private void setupTableView() {
        nomColumn.setCellValueFactory(new PropertyValueFactory<>("nom"));
        prenomColumn.setCellValueFactory(new PropertyValueFactory<>("prenom"));
        emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));

        // Configuration de la colonne Statut avec coloration conditionnelle
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
        statusColumn.setCellFactory(column -> new TableCell<Client, Status>() {
            @Override
            protected void updateItem(Status item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(item.toString());
                    if (item == Status.ACTIF) {
                        setStyle("-fx-text-fill: #2ecc71; -fx-alignment: center;");
                    } else {
                        setStyle("-fx-text-fill: #e74c3c; -fx-alignment: center;");
                    }
                }
            }
        });

        clientTable.setItems(clientData);
    }

    private void activateClient(Client client) {
         clientService.activeStatus(client.getEmail());
        compteService.creerCompte(client, Type.COURANT, BigDecimal.valueOf(5000.00)); // Adjust Type and balance as needed
    }

    private void loadData() {
        List<Client> clients = clientService.listerClientsParStatus(Status.EN_ATTENTE);
        clientData.addAll(clients);
        log.info("Chargement liste client: " + clients.size());
    }

    private void setupChart() {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        dataset.addValue(5, "Suspectes", "Janvier");
        dataset.addValue(3, "Suspectes", "Février");
        dataset.addValue(7, "Suspectes", "Mars");

        JFreeChart barChart = ChartFactory.createBarChart(
                "Transactions Suspectes",
                "Mois",
                "Nombre",
                dataset
        );

        ChartViewer viewer = new ChartViewer(barChart);
        viewer.setPrefSize(570, 250);
        chartPane.getChildren().add(viewer);
    }

    @FXML
    private void showClientDetails(ActionEvent event) {
        WindowManager.openParentAndChild("/fxml/admin/create_account.fxml", "Gestion des Clients");
    }

    @FXML
    private void showCompteDetails(ActionEvent event) {
        JFXDialogLayout dialogLayout = new JFXDialogLayout();
        dialogLayout.setHeading(new Label("Gestion des Comptes"));
        dialogLayout.setBody(new Label("Gestion des comptes à implémenter"));
        JFXDialog dialog = new JFXDialog(dialogRoot, dialogLayout, JFXDialog.DialogTransition.CENTER);
        dialog.show();
    }

    @FXML
    private void showTransactionDetails(ActionEvent event) {
        WindowManager.openParentAndChild("/fxml/admin/transaction_details.fxml", "Détails des Transactions");
    }

    @FXML
    private void showTicketDetails(ActionEvent event) {
        WindowManager.openParentAndChild("/fxml/admin/ticket_support.fxml", "Support Tickets");
    }
}