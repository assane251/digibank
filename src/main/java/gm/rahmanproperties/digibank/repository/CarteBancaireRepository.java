package gm.rahmanproperties.digibank.repository;

import gm.rahmanproperties.digibank.domain.CarteBancaire;
import gm.rahmanproperties.digibank.utils.JpaUtil;
import jakarta.persistence.EntityManager;

import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;

public class CarteBancaireRepository {

    private final EntityManager entityManager;
    private static final Logger log = Logger.getLogger(CarteBancaireRepository.class.getName());

    public CarteBancaireRepository() {
        this.entityManager = JpaUtil.getEntityManager();
    }

    public CarteBancaire save(CarteBancaire carte) {
        try {
            entityManager.getTransaction().begin();
            entityManager.persist(carte);
            entityManager.getTransaction().commit();
            log.info("Carte saved: " + carte.getNumero());
        } catch (Exception e) {
            entityManager.getTransaction().rollback();
            log.severe("Error saving carte: " + e.getMessage());
            throw e;
        }
        return carte;
    }
    public CarteBancaire findById(UUID id) {
        return entityManager.createQuery("SELECT c FROM CarteBancaire c WHERE c.id = :id", CarteBancaire.class)
                .setParameter("id", id)
                .getSingleResult();
    }

    public List<CarteBancaire> findByCompteId(UUID compteId) {
        return entityManager.createQuery("SELECT c FROM CarteBancaire c WHERE c.compte.id = :compteId", CarteBancaire.class)
                .setParameter("compteId", compteId)
                .getResultList();
    }

    public void update(CarteBancaire carte) {
        try {
            entityManager.getTransaction().begin();
            entityManager.merge(carte);
            entityManager.getTransaction().commit();
            log.info("Carte updated: " + carte.getNumero());
        } catch (Exception e) {
            entityManager.getTransaction().rollback();
            log.severe("Error updating carte: " + e.getMessage());
            throw e;
        }
    }

//    public CarteBancaire findByCarteBancaireJoinCompte(UUID carteId) {
//        return entityManager.createQuery("SELECT c FROM CarteBancaire c JOIN FETCH c.compte WHERE c.id = CAST(:carteId AS string)", CarteBancaire.class)
//                .setParameter("carteId", carteId.toString())
//                .getSingleResult();
//    }
}