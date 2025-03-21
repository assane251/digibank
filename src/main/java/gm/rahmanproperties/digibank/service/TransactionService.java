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

    public void createDepot(UUID compteId, BigDecimal montant) {
        Compte compte = compteService.findById(compteId);
        Transaction transaction = Transaction.builder()
                .type(TypeTransaction.DEPOT)
                .montant(montant)
                .date(LocalDateTime.now())
                .status(StatuTransaction.REUSSI)
                .compteSource(compte)
                .build();
        transactionRepository.save(transaction);

        compte.setSolde(compte.getSolde().add(montant));
        compteService.update(compte);
    }

    public void createRetrait(UUID compteId, BigDecimal montant) {
        Compte compte = compteService.findById(compteId);
        Transaction transaction = Transaction.builder()
                .type(TypeTransaction.RETRAIT)
                .montant(montant)
                .date(LocalDateTime.now())
                .status(StatuTransaction.REUSSI)
                .compteSource(compte)
                .build();
        transactionRepository.save(transaction);

        compte.setSolde(compte.getSolde().subtract(montant));
        compteService.update(compte);
    }

    public void createVirement(UUID compteSourceId, UUID compteDestId, BigDecimal montant) {
        Compte compteSource = compteService.findById(compteSourceId);
        Compte compteDest = compteService.findById(compteDestId);
        Transaction transaction = Transaction.builder()
                .type(TypeTransaction.VIREMENT)
                .montant(montant)
                .date(LocalDateTime.now())
                .status(StatuTransaction.REUSSI)
                .compteSource(compteSource)
                .compteDest(compteDest)
                .build();
        transactionRepository.save(transaction);

        compteSource.setSolde(compteSource.getSolde().subtract(montant));
        compteDest.setSolde(compteDest.getSolde().add(montant));
    }

    public List<Transaction> getTransactionsByCompte(UUID compteId) {
        return transactionRepository.findByCompteId(compteId);
    }
}