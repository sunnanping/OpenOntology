package com.by.open.ontology.adminservice;

import com.by.open.ontology.adminservice.entity.Admin;
import com.by.open.ontology.adminservice.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.ConfigurableApplicationContext;

import java.util.Date;
import java.util.Scanner;

@SpringBootApplication
@EnableDiscoveryClient
public class AdminServiceApplication implements CommandLineRunner {

    @Autowired
    private AdminService adminService;

    public static void main(String[] args) {
        // Check if we're running the create-admin-account command
        boolean isCreateAdminCommand = args.length > 0 && "create-admin-account".equals(args[0]);
        
        // Create SpringApplication with appropriate properties
        SpringApplication app = new SpringApplication(AdminServiceApplication.class);
        
        if (isCreateAdminCommand) {
            // Disable Eureka client for command line operation
            app.setAdditionalProfiles("command-line");
        }
        
        ConfigurableApplicationContext context = app.run(args);
        
        // If command line args are provided, shut down the application after processing
        if (isCreateAdminCommand) {
            context.close();
        }
    }

    @Override
    public void run(String... args) {
        if (args.length > 0 && "create-admin-account".equals(args[0])) {
            createAdminAccount();
        }
    }

    private void createAdminAccount() {
        Scanner scanner = new Scanner(System.in);
        
        System.out.println("=== Creating Admin Account ===");
        
        System.out.print("Enter username: ");
        String username = scanner.nextLine();
        
        System.out.print("Enter password: ");
        String password = scanner.nextLine();
        
        System.out.print("Enter email: ");
        String email = scanner.nextLine();
        
        System.out.print("Enter first name: ");
        String firstName = scanner.nextLine();
        
        System.out.print("Enter last name: ");
        String lastName = scanner.nextLine();
        
        System.out.print("Enter department (optional): ");
        String department = scanner.nextLine();
        
        System.out.print("Enter phone (optional): ");
        String phone = scanner.nextLine();
        
        try {
            Admin admin = new Admin();
            admin.setUsername(username);
            admin.setPassword(password);
            admin.setEmail(email);
            admin.setFirstName(firstName);
            admin.setLastName(lastName);
            admin.setDepartment(department);
            admin.setPhone(phone);
            admin.setEnabled(true);
            admin.setPermissions(new String[] {"USER_MANAGEMENT", "ONTOLOGY_MANAGEMENT", "SYSTEM_CONFIG"});
            admin.setCreatedDate(new Date());
            admin.setLastModifiedDate(new Date());
            
            Admin createdAdmin = adminService.createAdmin(admin);
            System.out.println("\nAdmin account created successfully!");
            System.out.println("Admin ID: " + createdAdmin.getId());
            System.out.println("Username: " + createdAdmin.getUsername());
            System.out.println("Email: " + createdAdmin.getEmail());
        } catch (Exception e) {
            System.err.println("Error creating admin account: " + e.getMessage());
        } finally {
            scanner.close();
        }
    }
}