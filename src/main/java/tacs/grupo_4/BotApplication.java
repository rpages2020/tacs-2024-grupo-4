package tacs.grupo_4;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class BotApplication {
    public static void main(String... args) {
        SpringApplication app = new SpringApplication(BotApplication.class);
        app.setAdditionalProfiles("bot");
        app.run(args);
    }
}
