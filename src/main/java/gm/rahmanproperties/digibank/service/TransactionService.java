package gm.rahmanproperties.digibank.service;

import gm.rahmanproperties.digibank.domain.Compte;
import gm.rahmanproperties.digibank.domain.Transaction;
import gm.rahmanproperties.digibank.enums.StatuTransaction;
import gm.rahmanproperties.digibank.enums.TypeTransaction;
import gm.rahmanproperties.digibank.repository.TransactionRepository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public class TransactionService {
    private final TransactionRepository transactionRepository;
    private final CompteService compteService;

    public TransactionService(TransactionRepository transactionRepository, CompteService compteService) {
        this.transactionRepository = transactionRepository;
        this.compteService = compteService;
    }

    public Transaction createDepot(UUID compteId, BigDecimal montant) {
        Compte compte = compteService.findById(compteId);
        if (compte == null) throw new IllegalArgumentException("Compte non trouvé");

        Transaction transaction = Transaction.builder()
                .id(UUID.randomUUID())
                .type(TypeTransaction.DEPOT)
                .montant(montant)
                .date(LocalDateTime.now())
                .status(StatuTransaction.REUSSI)
                .compteDest(compte)
                .build();

        compte.setSolde(compte.getSolde().add(montant));
        compteService.update(compte);
        return transactionRepository.save(transaction);
    }

    public Transaction createRetrait(UUID compteId, BigDecimal montant) {
        Compte compte = compteService.findById(compteId);
        if (compte == null) throw new IllegalArgumentException("Compte non trouvé");
        if (compte.getSolde().compareTo(montant) < 0)
            throw new IllegalStateException("Solde insuffisant");

        Transaction transaction = Transaction.builder()
                .id(UUID.randomUUID())
                .type(TypeTransaction.RETRAIT)
                .montant(montant)
                .date(LocalDateTime.now())
                .status(StatuTransaction.REUSSI)
                .compteSource(compte)
                .build();

        compte.setSolde(compte.getSolde().subtract(montant));
        compteService.update(compte);
        return transactionRepository.save(transaction);
    }

    public Transaction createVirement(UUID compteSourceId, UUID compteDestId, BigDecimal montant) {
        Compte compteSource = compteService.findById(compteSourceId);
        Compte compteDest = compteService.findById(compteDestId);

        if (compteSource == null || compteDest == null)
            throw new IllegalArgumentException("Compte source ou destination non trouvé");
        if (compteSource.getSolde().compareTo(montant) < 0)
            throw new IllegalStateException("Solde insuffisant");

        Transaction transaction = Transaction.builder()
                .id(UUID.randomUUID())
                .type(TypeTransaction.VIREMENT)
                .montant(montant)
                .date(LocalDateTime.now())
                .status(StatuTransaction.REUSSI)
                .compteSource(compteSource)
                .compteDest(compteDest)
                .build();

        compteSource.setSolde(compteSource.getSolde().subtract(montant));
        compteDest.setSolde(compteDest.getSolde().add(montant));
        compteService.update(compteSource);
        compteService.update(compteDest);
        return transactionRepository.save(transaction);
    }

    public List<Transaction> getTransactionsByCompte(UUID compteId) {
        return transactionRepository.findByCompteId(compteId);
    }
}