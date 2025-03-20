package gm.rahmanproperties.digibank.repository;

import gm.rahmanproperties.digibank.domain.Remboursement;
import gm.rahmanproperties.digibank.utils.JpaUtil;
import jakarta.persistence.EntityManager;

import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;

public class RemboursementRepository {

    private final EntityManager entityManager;
    private static final Logger log = Logger.getLogger(CreditRepository.class.getName());

    public RemboursementRepository() {
        this.entityManager = JpaUtil.getEntityManager();
    }

    public Remboursement save(Remboursement remboursement) {
        try {
            entityManager.getTransaction().begin();
            entityManager.persist(remboursement);
            entityManager.getTransaction().commit();
            log.info("Remboursement saved: " + remboursement.getMontant());
        } catch (Exception e) {
            entityManager.getTransaction().rollback();
            log.severe("Error saving remboursement: " + e.getMessage());
            throw e;
        }
        return remboursement;
    }

    public List<Remboursement> findByCreditId(UUID creditId) {
        return entityManager.createQuery("SELECT r FROM Remboursement r WHERE r.credit.id = :creditId", Remboursement.class)
                .setParameter("creditId", creditId)
                .getResultList();
    }
}