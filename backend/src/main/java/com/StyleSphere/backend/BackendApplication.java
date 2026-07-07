package com.StyleSphere.backend;

import com.StyleSphere.backend.model.Product;
import com.StyleSphere.backend.model.User;
import com.StyleSphere.backend.repository.ProductRepository;
import com.StyleSphere.backend.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@SpringBootApplication
public class BackendApplication {


	@Bean
	CommandLineRunner initDatabase(UserRepository repository) {
		return args -> {

			if (repository.count() == 0){
				User omar = new User();
				omar.setEmail("omarharz553@gmail.com");
				omar.setPassword("$2a$12$aNqpMYUD8HWv3jEvilv4LORGvmea2PSoPODt5zDBzihs7bXkJlGfq");
				Set<String> roles = new HashSet<>();
				roles.add("ADMIN");
				roles.add("USER");
				omar.setRoles(roles);
				repository.save(omar);
				System.out.println("the user omar is added");
			}



		};
	}

	public static void main(String[] args) {
		SpringApplication.run(BackendApplication.class, args);
	}

}
