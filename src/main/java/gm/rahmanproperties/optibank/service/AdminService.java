package gm.rahmanproperties.optibank.service;

import gm.rahmanproperties.optibank.domain.Admin;
import gm.rahmanproperties.optibank.dtos.AdminDto;
import gm.rahmanproperties.optibank.mappers.AdminMapper;
import gm.rahmanproperties.optibank.repository.AdminRepository;
import org.mindrot.jbcrypt.BCrypt;

public class AdminService {
    private final AdminRepository adminRepository = new AdminRepository();

    public Admin authenticateAdmin(String username) {
        AdminDto adminDto = adminRepository.authenticate(username);
        return AdminMapper.toEntity(adminDto);
    }

    public void ensureDefaultAdminExists() {
        if (adminRepository.existsByUsername("admin")) {
            createDefaultAdmin();
        }
    }

    public void createDefaultAdmin() {
        String hashedPassword = BCrypt.hashpw("admin123", BCrypt.gensalt());
        AdminDto adminDto = AdminDto.builder()
                .username("admin")
                .password(hashedPassword)
                .role("super_admin")
                .firstLogin(true)
                .build();
        Admin admin = AdminMapper.toEntity(adminDto);
        adminRepository.save(admin);
    }

    public void updateAdminPassword(String username, String password) {
        Admin admin = authenticateAdmin(username);
        if (admin == null) {
            throw new IllegalArgumentException("Admin not found for username: " + username);
        }
        String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());
        admin.setPassword(hashedPassword);
        admin.setFirstLogin(false);
        adminRepository.updatePassword(admin);
    }
}