package com.by.open.ontology.adminservice.service;

import com.by.open.ontology.adminservice.entity.Admin;
import com.by.open.ontology.adminservice.entity.SystemSettings;
import com.by.open.ontology.adminservice.repository.AdminRepository;
import com.by.open.ontology.adminservice.repository.SystemSettingsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class AdminService {
    @Autowired
    private AdminRepository adminRepository;

    @Autowired
    private SystemSettingsRepository systemSettingsRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    public Admin createAdmin(Admin admin) {
        if (adminRepository.existsByUsername(admin.getUsername())) {
            throw new RuntimeException("Admin username already exists");
        }

        if (adminRepository.existsByEmail(admin.getEmail())) {
            throw new RuntimeException("Admin email already exists");
        }

        admin.setPassword(passwordEncoder.encode(admin.getPassword()));

        Date now = new Date();
        admin.setCreatedDate(now);
        admin.setLastModifiedDate(now);

        if (admin.getPermissions() == null || admin.getPermissions().length == 0) {
            admin.setPermissions(new String[] {"USER_MANAGEMENT", "ONTOLOGY_MANAGEMENT", "SYSTEM_CONFIG"});
        }

        admin.setEnabled(true);

        return adminRepository.save(admin);
    }

    public Admin login(String username, String password) {
        Admin admin = adminRepository.findByUsername(username);
        if (admin == null) {
            throw new RuntimeException("Admin not found");
        }

        if (!passwordEncoder.matches(password, admin.getPassword())) {
            throw new RuntimeException("Invalid password");
        }

        if (!admin.isEnabled()) {
            throw new RuntimeException("Admin is disabled");
        }

        return admin;
    }

    public Admin findByUsername(String username) {
        return adminRepository.findByUsername(username);
    }

    public Admin findByEmail(String email) {
        return adminRepository.findByEmail(email);
    }

    public List<Admin> findAll() {
        return adminRepository.findAll();
    }

    public Admin findById(String id) {
        return adminRepository.findById(id).orElseThrow(() -> new RuntimeException("Admin not found"));
    }

    public Admin update(Admin admin) {
        Admin existingAdmin = adminRepository.findById(admin.getId())
                .orElseThrow(() -> new RuntimeException("Admin not found"));

        if (admin.getUsername() != null) {
            existingAdmin.setUsername(admin.getUsername());
        }
        if (admin.getPassword() != null) {
            existingAdmin.setPassword(passwordEncoder.encode(admin.getPassword()));
        }
        if (admin.getEmail() != null) {
            existingAdmin.setEmail(admin.getEmail());
        }
        if (admin.getFirstName() != null) {
            existingAdmin.setFirstName(admin.getFirstName());
        }
        if (admin.getLastName() != null) {
            existingAdmin.setLastName(admin.getLastName());
        }
        if (admin.getDepartment() != null) {
            existingAdmin.setDepartment(admin.getDepartment());
        }
        if (admin.getPhone() != null) {
            existingAdmin.setPhone(admin.getPhone());
        }
        if (admin.getPermissions() != null) {
            existingAdmin.setPermissions(admin.getPermissions());
        }
        existingAdmin.setEnabled(admin.isEnabled());
        existingAdmin.setLastModifiedDate(new Date());

        return adminRepository.save(existingAdmin);
    }

    public void delete(String id) {
        adminRepository.deleteById(id);
    }

    public Admin addPermission(String adminId, String permission) {
        Admin admin = findById(adminId);
        String[] currentPermissions = admin.getPermissions();
        String[] newPermissions = new String[currentPermissions.length + 1];
        System.arraycopy(currentPermissions, 0, newPermissions, 0, currentPermissions.length);
        newPermissions[currentPermissions.length] = permission;
        admin.setPermissions(newPermissions);
        admin.setLastModifiedDate(new Date());
        return adminRepository.save(admin);
    }

    public Admin removePermission(String adminId, String permission) {
        Admin admin = findById(adminId);
        String[] currentPermissions = admin.getPermissions();
        String[] newPermissions = new String[currentPermissions.length - 1];
        int index = 0;
        for (String perm : currentPermissions) {
            if (!perm.equals(permission)) {
                newPermissions[index++] = perm;
            }
        }
        admin.setPermissions(newPermissions);
        admin.setLastModifiedDate(new Date());
        return adminRepository.save(admin);
    }

    public SystemSettings getSystemSettings() {
        SystemSettings settings = systemSettingsRepository.findFirstByOrderByCreatedDateDesc();
        if (settings == null) {
            settings = new SystemSettings();
            settings.setScheme("http");
            settings.setHost("localhost");
            settings.setPath("");
            settings.setPort("8080");
            settings.setAccountCreationEnabled(true);
            settings.setProjectCreationEnabled(true);
            settings.setProjectUploadEnabled(true);
            settings.setMaxUploadSize(50);
            Date now = new Date();
            settings.setCreatedDate(now);
            settings.setLastModifiedDate(now);
            settings = systemSettingsRepository.save(settings);
        }
        return settings;
    }

    public SystemSettings updateSystemSettings(SystemSettings settings) {
        Date now = new Date();
        settings.setLastModifiedDate(now);
        if (settings.getId() == null) {
            settings.setCreatedDate(now);
        }
        return systemSettingsRepository.save(settings);
    }
}