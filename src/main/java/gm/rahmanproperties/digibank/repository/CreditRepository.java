package gm.rahmanproperties.digibank.repository;

import gm.rahmanproperties.digibank.domain.Credit;
import gm.rahmanproperties.digibank.utils.JpaUtil;
import jakarta.persistence.EntityManager;

import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;

public class CreditRepository {

    private final EntityManager entityManager;
    private static final Logger log = Logger.getLogger(CreditRepository.class.getName());

    public CreditRepository() {
        this.entityManager = JpaUtil.getEntityManager();
    }

    public Credit save(Credit credit) {
        try {
            entityManager.getTransaction().begin();
            entityManager.persist(credit);
            entityManager.getTransaction().commit();
            log.info("Credit saved: " + credit.getMontant());
        } catch (Exception e) {
            entityManager.getTransaction().rollback();
            log.severe("Error saving credit: " + e.getMessage());
            throw e;
        }
        return credit;
    }
    public Credit findById(UUID id) {
        return entityManager.createQuery("SELECT c FROM Credit c WHERE c.id = :id", Credit.class)
                .setParameter("id", id)
                .getSingleResult();
    }

    public List<Credit> findByClientId(UUID clientId) {
        return entityManager.createQuery("SELECT c FROM Credit c WHERE c.client.id = :clientId", Credit.class)
                .setParameter("clientId", clientId)
                .getResultList();
    }

    public void update(Credit credit) {
        try {
            entityManager.getTransaction().begin();
            entityManager.merge(credit);
            entityManager.getTransaction().commit();
            log.info("Credit updated: " + credit.getMontant());
        } catch (Exception e) {
            entityManager.getTransaction().rollback();
            log.severe("Error updating credit: " + e.getMessage());
            throw e;
        }
    }
}