package gm.rahmanproperties.digibank.repository;

import gm.rahmanproperties.digibank.domain.Transaction;
import gm.rahmanproperties.digibank.utils.JpaUtil;
import jakarta.persistence.EntityManager;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;

public class TransactionRepository {

    private static final Logger log = Logger.getLogger(TransactionRepository.class.getName());
    private final EntityManager entityManager;

    public TransactionRepository() {
        this.entityManager = JpaUtil.getEntityManager();
    }

    public Transaction save(Transaction transaction) {
        try {
            entityManager.getTransaction().begin();
            entityManager.persist(transaction);
            entityManager.getTransaction().commit();
            log.info("Transaction saved: " + transaction.getStatus());
        } catch (Exception e) {
            entityManager.getTransaction().rollback();
            log.severe("Error d'enregistrement de transaction: " + e.getMessage());
            throw e;
        }
        return transaction;
    }

    public List<Transaction> findByCompteId(UUID compteId) {
        return entityManager.createQuery("SELECT t FROM Transaction t WHERE t.compteSource.id = :compteId OR t.compteDest.id = :compteId", Transaction.class)
                .setParameter("compteId", compteId)
                .getResultList();
    }
}
