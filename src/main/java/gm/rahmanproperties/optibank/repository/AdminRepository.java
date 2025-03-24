package gm.rahmanproperties.optibank.repository;

import gm.rahmanproperties.optibank.domain.Admin;
import gm.rahmanproperties.optibank.dtos.AdminDto;
import gm.rahmanproperties.optibank.mappers.AdminMapper;
import gm.rahmanproperties.optibank.utils.JpaUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import java.util.logging.Logger;

public class AdminRepository {

    private static final Logger log = Logger.getLogger(AdminRepository.class.getName());

    @PersistenceContext
    EntityManager entityManager = JpaUtil.getEntityManager();

    public AdminDto authenticate(String username) {
        log.info("Authenticating admin with username: " + username);
        Admin admin = entityManager.createQuery("SELECT a FROM Admin a WHERE a.username = :username", Admin.class)
                .setParameter("username", username)
                .getSingleResult();
        return AdminMapper.toDto(admin);
    }

    public boolean existsByUsername(String username) {
        Long count = entityManager.createQuery("SELECT COUNT(a) FROM Admin a WHERE a.username = :username", Long.class)
                .setParameter("username", username)
                .getSingleResult();
        return count <= 0;
    }

    public void save(Admin admin) {
        entityManager.getTransaction().begin();
        entityManager.persist(admin);
        entityManager.getTransaction().commit();
    }

    public void updatePassword(Admin admin) {
        if (admin.getId() == null) {
            throw new IllegalArgumentException("L'id de l'admin ne peut pas être null");
        }

        entityManager.getTransaction().begin();
        entityManager.merge(admin);
        entityManager.getTransaction().commit();
    }
}
