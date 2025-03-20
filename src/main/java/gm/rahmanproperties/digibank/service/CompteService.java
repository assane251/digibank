package gm.rahmanproperties.digibank.service;

import gm.rahmanproperties.digibank.domain.Client;
import gm.rahmanproperties.digibank.domain.Compte;
import gm.rahmanproperties.digibank.domain.FraisBancaire;
import gm.rahmanproperties.digibank.enums.Type;
import gm.rahmanproperties.digibank.repository.CompteRepository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Random;
import java.util.UUID;

public class CompteService {
    private final CompteRepository compteRepository;

    public CompteService() {
        this.compteRepository = new CompteRepository();
    }

    // Create a new account for a client
    public Compte creerCompte(Client client, Type type, BigDecimal soldeInitial) {
        String numeroCompte = generateUniqueAccountNumber();
//        BigDecimal soldeInitial = determineInitialBalance(type);

        Compte compte = Compte.builder()
                .numero(numeroCompte)
                .type(type)
                .solde(soldeInitial)
                .dateCreation(LocalDate.now())
                .client(client)
                .build();

        compteRepository.save(compte);
        appliquerFraisInitial(compte);
        return compte;
    }

    private String generateUniqueAccountNumber() {
        Random random = new Random();
        StringBuilder numero;
        do {
            numero = new StringBuilder("DB");
            for (int i = 0; i < 14; i++) {
                numero.append(random.nextInt(10));
            }
        } while (compteRepository.existsByNumero(numero.toString()));
        return numero.toString();
    }

    // Determine initial balance based on account type
    private BigDecimal determineInitialBalance(Type type) {
        return switch (type) {
            case COURANT -> BigDecimal.ZERO;
            case EPARGNE -> new BigDecimal("1000.00");
        };
    }

    private void appliquerFraisInitial(Compte compte) {
        if (compte.getType() == Type.COURANT) {
            FraisBancaire frais = FraisBancaire.builder()
                    .type("Frais d'ouverture")
                    .montant(new BigDecimal("500.00"))
                    .dateApplication(LocalDate.now())
                    .compte(compte)
                    .build();
            compteRepository.saveFraisBancaire(frais);
            compte.setSolde(compte.getSolde().subtract(frais.getMontant()));
            compteRepository.update(compte);
        }
    }

    // Apply monthly fees (could be called periodically)
    public void appliquerFraisMensuel(Compte compte) {
        BigDecimal fraisMensuel = compte.getType() == Type.COURANT ? new BigDecimal("200.00") : new BigDecimal("500.00");
        FraisBancaire frais = FraisBancaire.builder()
                .type("Frais mensuels")
                .montant(fraisMensuel)
                .dateApplication(LocalDate.now())
                .compte(compte)
                .build();
        compteRepository.saveFraisBancaire(frais);
        compte.setSolde(compte.getSolde().subtract(fraisMensuel));
        compteRepository.update(compte);
    }

    public Compte findById(UUID compteId) {
        return compteRepository.findById(compteId);
    }

    public Compte findByNumero(String numero) {
        return compteRepository.findByNumero(numero);
    }

    public void update(Compte compte) {
        compteRepository.update(compte);
    }
}