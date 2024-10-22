package tacs.grupo_4;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Grupo4Application {
	public static void main(String... args) {
		SpringApplication app = new SpringApplication(Grupo4Application.class);
		app.setAdditionalProfiles("api");
		app.run(args);
	}
}
