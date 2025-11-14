package com.store.grocerystore;

import org.springframework.boot.SpringApplication;

public class TestGrocerystoreApplication {

	public static void main(String[] args) {
		SpringApplication.from(GrocerystoreApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}
