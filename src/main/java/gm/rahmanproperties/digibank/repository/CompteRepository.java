package gm.rahmanproperties.digibank.repository;

import gm.rahmanproperties.digibank.domain.Compte;
import gm.rahmanproperties.digibank.domain.FraisBancaire;
import gm.rahmanproperties.digibank.utils.JpaUtil;
import jakarta.persistence.EntityManager;

import java.math.BigDecimal;
import java.util.UUID;
import java.util.logging.Logger;

public class CompteRepository {
    private static final Logger log = Logger.getLogger(CompteRepository.class.getName());
    private final EntityManager entityManager;

    public CompteRepository() {
        this.entityManager = JpaUtil.getEntityManager();
    }

    public void save(Compte compte) {
        try {
            entityManager.getTransaction().begin();
            entityManager.persist(compte);
            entityManager.getTransaction().commit();
            log.info("Compte saved: " + compte.getNumero());
        } catch (Exception e) {
            entityManager.getTransaction().rollback();
            log.severe("Error saving compte: " + e.getMessage());
            throw e;
        }
    }

    public void update(Compte compte) {
        try {
            entityManager.getTransaction().begin();
            entityManager.merge(compte);
            entityManager.getTransaction().commit();
            log.info("Compte updated: " + compte.getNumero());
        } catch (Exception e) {
            entityManager.getTransaction().rollback();
            log.severe("Error updating compte: " + e.getMessage());
            throw e;
        }
    }

    public boolean existsByNumero(String numero) {
        try {
            entityManager.createQuery("SELECT c FROM Compte c WHERE c.numero = :numero", Compte.class)
                    .setParameter("numero", numero)
                    .getSingleResult();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public void saveFraisBancaire(FraisBancaire frais) {
        try {
            entityManager.getTransaction().begin();
            entityManager.persist(frais);
            entityManager.getTransaction().commit();
            log.info("FraisBancaire saved for compte: " + frais.getCompte().getNumero());
        } catch (Exception e) {
            entityManager.getTransaction().rollback();
            log.severe("Error saving fraisBancaire: " + e.getMessage());
            throw e;
        }
    }

    public Compte findById(UUID compteId) {
        return entityManager.createQuery("SELECT c FROM Compte c WHERE c.id = :id", Compte.class)
                .setParameter("id", compteId)
                .getSingleResult();
    }

    public Compte findByNumero(String numero) {
        return entityManager.createQuery("SELECT c FROM Compte c WHERE c.numero = :numero", Compte.class)
                .setParameter("numero", numero)
                .getSingleResult();
    }
}