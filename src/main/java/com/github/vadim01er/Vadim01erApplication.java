package com.github.vadim01er;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class Vadim01erApplication {

	public static void main(String[] args) {
		SpringApplication.run(Vadim01erApplication.class, args);
	}

}
