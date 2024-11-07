package com.hilmatrix.simplenotes;

import com.hilmatrix.simplenotes.config.RsaKeyConfigProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@SpringBootApplication
@EnableConfigurationProperties({RsaKeyConfigProperties.class})
public class SimplenotesApplication {

	public static void main(String[] args) {
		SpringApplication.run(SimplenotesApplication.class, args);
		System.out.println("Yak, jalan");

		BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
		String plainPassword = "12345678";
		String passwordHash = passwordEncoder.encode(plainPassword);
		System.out.println("Password Hash: " + passwordHash);
	}

}
