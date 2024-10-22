package tacs.grupo_4.configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springdoc.core.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Mi API")
                        .version("1.0.0")
                        .description("Descripción de mi API")
                        .termsOfService("http://ejemplo.com/terms")
                        .contact(new Contact()
                                .name("Nombre del Contacto")
                                .url("http://ejemplo.com")
                                .email("contacto@ejemplo.com"))
                        .license(new License()
                                .name("Licencia de la API")
                                .url("http://ejemplo.com/license")))
                .addServersItem(new Server().url("http://localhost:8080").description("Servidor local"));
    }
    @Bean
    public GroupedOpenApi publicApi() {
        return GroupedOpenApi.builder()
                .group("public")
                .pathsToMatch("/**")
                .build();
    }
}
