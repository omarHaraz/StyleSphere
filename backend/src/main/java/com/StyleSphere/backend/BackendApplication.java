package com.StyleSphere.backend;

import com.StyleSphere.backend.auth.service.EmailService;
import com.StyleSphere.backend.user.model.User;
import com.StyleSphere.backend.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@SpringBootApplication
public class BackendApplication {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private BCryptPasswordEncoder passwordEncoder;

	@Bean
	CommandLineRunner initDatabase(UserRepository repository) {
		return args -> {

			if (userRepository.count() == 0) {
				System.out.println("--- Seeding Initial Test Users and Admins ---");

				// 1. Create a Super Admin (Can manage other admins)
				User superAdmin = new User();
				superAdmin.setName("Super Admin");
				superAdmin.setEmail("superadmin@stylesphere.com");
				superAdmin.setPassword(passwordEncoder.encode("admin123"));
				superAdmin.setEnabled(true);
				Set<String> superAdminRoles = new HashSet<>();
				superAdminRoles.add("ROLE_SUPER_ADMIN");
				superAdminRoles.add("ROLE_ADMIN");
				superAdmin.setRoles(superAdminRoles);
				userRepository.save(superAdmin);

				// 2. Create a Regular Admin (Can handle dashboard but not edit admins)
				User regularAdmin = new User();
				regularAdmin.setName("Jane Admin");
				regularAdmin.setEmail("admin@stylesphere.com");
				regularAdmin.setPassword(passwordEncoder.encode("admin123"));
				regularAdmin.setEnabled(true);
				Set<String> adminRoles = new HashSet<>();
				adminRoles.add("ROLE_ADMIN");
				regularAdmin.setRoles(adminRoles);
				userRepository.save(regularAdmin);

				// 3. Create a Regular Customer
				User customer = new User();
				customer.setName("John Doe");
				customer.setEmail("customer@stylesphere.com");
				customer.setPassword(passwordEncoder.encode("password123"));
				customer.setEnabled(true);
				Set<String> customerRoles = new HashSet<>();
				customerRoles.add("ROLE_CUSTOMER");
				customer.setRoles(customerRoles);
				userRepository.save(customer);

				System.out.println("--- Seeding Completed Successfully! ---");
			} else {
				System.out.println("--- Database already has data. Skipping seeder. ---");
			}


		};
	}

	public static void main(String[] args) {
		SpringApplication.run(BackendApplication.class, args);
	}

}
