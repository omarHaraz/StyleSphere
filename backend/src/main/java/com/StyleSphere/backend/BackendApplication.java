package com.StyleSphere.backend;

import com.StyleSphere.backend.model.Product;
import com.StyleSphere.backend.repository.ProductRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.Arrays;
import java.util.List;

@SpringBootApplication
public class BackendApplication {


	@Bean
	CommandLineRunner initDatabase(ProductRepository repository) {
		return args -> {
			// Only seed if the database is empty
			if (repository.count() == 0) {
				Product tShirt = new Product();
				tShirt.setName("White T-Shirt");
				tShirt.setDescription("Premium cotton oversized fit");
				tShirt.setPrice(450.0);
				tShirt.setCategory("T-Shirts");
				tShirt.setStockQuantity(10);
				// Assuming you have a list of strings in your model for images
				List<String> images = Arrays.asList(
						"images/products/t-shirt/white/1.jpg",
						"images/products/t-shirt/white/2.jpg",
						"images/products/t-shirt/white/3.jpg",
						"images/products/t-shirt/white/4.jpg",
						"images/products/t-shirt/white/5.jpg",
						"images/products/t-shirt/white/6.jpg"
				);
				tShirt.setImageUrls(images);

				repository.save(tShirt);
				System.out.println("Data Seeding Complete: White T-Shirt added.");



				Product poloShirt = new Product();
				poloShirt.setName("White Polo Shirt");
				poloShirt.setDescription("Premium cotton oversized fit");
				poloShirt.setPrice(450.0);
				poloShirt.setCategory("Polo Shirts");
				poloShirt.setStockQuantity(0);
				// Assuming you have a list of strings in your model for images
				List<String> images1 = Arrays.asList(
						"images/products/polo shirt/white/1.jpg",
						"images/products/polo shirt/white/2.jpg",
						"images/products/polo shirt/white/3.jpg",
						"images/products/polo shirt/white/4.jpg",
						"images/products/polo shirt/white/5.jpg"
				);
				poloShirt.setImageUrls(images1);

				repository.save(poloShirt);
				System.out.println("Data Seeding Complete: White polo shirt added.");





			}
		};
	}

	public static void main(String[] args) {
		SpringApplication.run(BackendApplication.class, args);
	}

}
