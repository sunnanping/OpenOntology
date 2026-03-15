package com.by.open.ontology.adminservice.controller;

import com.by.open.ontology.adminservice.entity.Admin;
import com.by.open.ontology.adminservice.entity.SystemSettings;
import com.by.open.ontology.adminservice.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
public class AdminController {
    @Autowired
    private AdminService adminService;

    @PostMapping("/create")
    public Admin createAdmin(@RequestBody Admin admin) {
        return adminService.createAdmin(admin);
    }

    @PostMapping("/login")
    public Admin login(@RequestParam String username, @RequestParam String password) {
        return adminService.login(username, password);
    }

    @GetMapping("/findByUsername")
    public Admin findByUsername(@RequestParam String username) {
        return adminService.findByUsername(username);
    }

    @GetMapping("/findByEmail")
    public Admin findByEmail(@RequestParam String email) {
        return adminService.findByEmail(email);
    }

    @GetMapping("/findAll")
    public List<Admin> findAll() {
        return adminService.findAll();
    }

    @GetMapping("/findById/{id}")
    public Admin findById(@PathVariable String id) {
        return adminService.findById(id);
    }

    @PutMapping("/update")
    public Admin update(@RequestBody Admin admin) {
        return adminService.update(admin);
    }

    @DeleteMapping("/delete/{id}")
    public void delete(@PathVariable String id) {
        adminService.delete(id);
    }

    @PostMapping("/add-permission")
    public Admin addPermission(@RequestParam String adminId, @RequestParam String permission) {
        return adminService.addPermission(adminId, permission);
    }

    @DeleteMapping("/remove-permission")
    public Admin removePermission(@RequestParam String adminId, @RequestParam String permission) {
        return adminService.removePermission(adminId, permission);
    }

    @GetMapping("/settings")
    public SystemSettings getSystemSettings() {
        return adminService.getSystemSettings();
    }

    @PutMapping("/settings")
    public SystemSettings updateSystemSettings(@RequestBody SystemSettings settings) {
        return adminService.updateSystemSettings(settings);
    }
}