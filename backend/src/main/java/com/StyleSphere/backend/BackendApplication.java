package com.StyleSphere.backend;

import com.StyleSphere.backend.auth.repository.UserRepository;
import com.StyleSphere.backend.auth.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
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

	@Autowired
	private EmailService emailService;

	@Bean
	CommandLineRunner initDatabase(UserRepository repository) {
		return args -> {


		};
	}

	public static void main(String[] args) {
		SpringApplication.run(BackendApplication.class, args);
	}

}
