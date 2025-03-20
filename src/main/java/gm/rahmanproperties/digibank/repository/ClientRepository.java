package gm.rahmanproperties.digibank.repository;

import gm.rahmanproperties.digibank.domain.Client;
import gm.rahmanproperties.digibank.dtos.ClientDto;
import gm.rahmanproperties.digibank.enums.Status;
import gm.rahmanproperties.digibank.mappers.ClientMapper;
import gm.rahmanproperties.digibank.utils.JpaUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;

import java.util.List;
import java.util.logging.Logger;

public class ClientRepository {
    private static final Logger log = Logger.getLogger(ClientRepository.class.getName());
    private final EntityManager entityManager;

    public ClientRepository() {
        this.entityManager = JpaUtil.getEntityManager();
    }

    public ClientDto authenticate(String email) {
        try {
            log.info("Authenticating client with email: " + email);
            Client client = entityManager.createQuery("SELECT c FROM Client c WHERE c.email = :email", Client.class)
                    .setParameter("email", email)
                    .getSingleResult();
            return ClientMapper.toDto(client);
        } catch (NoResultException e) {
            log.warning("No client found with email: " + email);
            return null;
        }
    }

    public List<Client> findByStatus(Status status) {
        return entityManager.createQuery("SELECT c FROM Client c WHERE c.status = :status", Client.class)
                    .setParameter("status", status)
                    .getResultList();
    }

    public Client existsByEmail(String email) {
        try {
            return entityManager.createQuery("SELECT c FROM Client c WHERE c.email = :email", Client.class)
                    .setParameter("email", email)
                    .getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    public void save(Client client) {
        try {
            entityManager.getTransaction().begin();
            entityManager.persist(client);
            entityManager.getTransaction().commit();
            log.info("Client saved: " + client.getEmail());
        } catch (Exception e) {
            entityManager.getTransaction().rollback();
            log.severe("Error saving client: " + e.getMessage());
            throw e;
        }
    }

    public void update(Client client) {
        try {
            entityManager.getTransaction().begin();
            entityManager.merge(client);
            entityManager.getTransaction().commit();
            log.info("Client updated: " + client.getEmail());
        } catch (Exception e) {
            entityManager.getTransaction().rollback();
            log.severe("Error updating client: " + e.getMessage());
            throw e;
        }
    }

    public List<Client> findAll() {
        return entityManager.createQuery("SELECT c FROM Client c", Client.class)
                .getResultList();
    }
}