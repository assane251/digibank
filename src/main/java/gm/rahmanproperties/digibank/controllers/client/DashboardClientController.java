package gm.rahmanproperties.digibank.controllers.client;

import com.jfoenix.controls.JFXButton;
import gm.rahmanproperties.digibank.domain.Client;
import gm.rahmanproperties.digibank.domain.Compte;
import gm.rahmanproperties.digibank.enums.Type;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.VBox;

import java.math.BigDecimal;
import java.net.URL;
import java.util.ResourceBundle;

public class DashboardClientController implements Initializable {

    @FXML private Label totalBalance;
    @FXML private TableView<Compte> accountsTable;
    @FXML private TableColumn<Compte, String> accountNumberCol;
    @FXML private TableColumn<Compte, Type> accountTypeCol;
    @FXML private TableColumn<Compte, BigDecimal> accountBalanceCol;
    @FXML private VBox chartContainer;
    @FXML private JFXButton transactionsBtn;
    @FXML private JFXButton creditsBtn;
    @FXML private JFXButton cardsBtn;
    @FXML private JFXButton supportBtn;

    private final ObservableList<Compte> accountData = FXCollections.observableArrayList();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
//        // Setup TableView columns
//        accountNumberCol.setCellValueFactory(new PropertyValueFactory<>("numero"));
//        accountTypeCol.setCellValueFactory(new PropertyValueFactory<>("type"));
//        accountBalanceCol.setCellValueFactory(new PropertyValueFactory<>("solde"));
//        accountsTable.setItems(accountData);
//
//        // Button actions
//        transactionsBtn.setOnAction(e -> navigateTo("transactions"));
//        creditsBtn.setOnAction(e -> navigateTo("credits"));
//        cardsBtn.setOnAction(e -> navigateTo("cards"));
//        supportBtn.setOnAction(e -> navigateTo("support"));
    }

    public void loadClientData(Client client) {
        // Load accounts
//        List<Compte> comptes = client.getComptes();
//        accountData.setAll(comptes);
//
//        // Calculate and display total balance
//        BigDecimal total = comptes.stream()
//                .map(Compte::getSolde)
//                .reduce(BigDecimal.ZERO, BigDecimal::add);
//        totalBalance.setText("$" + total.toString());
//
//        // Create and display pie chart
//        DefaultPieDataset dataset = new DefaultPieDataset();
//        comptes.forEach(compte ->
//                dataset.setValue(compte.getNumero(), compte.getSolde().doubleValue()));
//
//        JFreeChart chart = ChartFactory.createPieChart(
//                "Répartition des soldes",
//                dataset,
//                true,
//                true,
//                false
//        );
//
//        ChartViewer viewer = new ChartViewer(chart);
//        chartContainer.getChildren().setAll(viewer);
//    }
//
//    private void navigateTo(String section) {
//        // Implement navigation logic here
//        System.out.println("Navigating to: " + section);
//        // You would typically use a Scene manager or similar pattern to switch views
    }
}