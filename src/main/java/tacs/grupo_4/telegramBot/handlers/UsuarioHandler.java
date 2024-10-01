package tacs.grupo_4.telegramBot.handlers;

import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import tacs.grupo_4.entities.Usuario;
import tacs.grupo_4.exceptions.UsuarioNotFoundException;
import tacs.grupo_4.telegramBot.ImpresoraJSON;
import tacs.grupo_4.telegramBot.TelegramBot;

import java.util.UUID;

@Component
public class UsuarioHandler {

    private final WebClient webClient;
    private final TelegramBot telegramBot;

    public UsuarioHandler(WebClient.Builder webClientBuilder, TelegramBot telegramBot) {
        this.webClient = webClientBuilder.build();
        this.telegramBot = telegramBot;
    }

    public String whoami(String[] mensaje, String chatId, Long telegramUserId) {
        if (mensaje.length != 0) {
            return "No se esperaban parámetros";
        }

        Mono<Usuario> responseMono = findByTelegramId(telegramUserId);

        responseMono.subscribe(
                response -> telegramBot.enviarMensaje(chatId,
                        ImpresoraJSON.imprimir(response)),
                error -> telegramBot.enviarMensaje(chatId,
                        "Hubo un error: no tiene una cuenta")
        );

        return "";
    }

    public String crearUsuario(String[] mensaje, String chatId, Long telegramUserId) {
        if (mensaje.length != 3) {
            return "crearUsuario <nombre>,<email>,<dni>";
        }
        String url = telegramBot.getEnvBaseUrl() + ":8080/api/usuarios";

        Usuario usuario = Usuario.builder()
                .id(UUID.randomUUID())
                .nombre(mensaje[0])
                .email(mensaje[1])
                .dni(Integer.parseInt(mensaje[2]))
                .telegramUserId(telegramUserId)
                .build();

        Mono<Usuario> responseMono = webClient.post()
                .uri(url)
                .bodyValue(usuario)
                .retrieve()
                .onStatus(status -> status.value() == 409, clientResponse -> {
                    return Mono.just(new RuntimeException("El usuario ya existe."));
                })
                .bodyToMono(Usuario.class);

        responseMono.subscribe(
                response -> telegramBot.enviarMensaje(chatId,
                        "Usuario creado exitosamente\n" + ImpresoraJSON.imprimir(response)),
                error -> telegramBot.enviarMensaje(chatId,
                        "Hubo un error: " + error.getMessage())
        );

        return "";
    }

    public Mono<Usuario> findByTelegramId(Long telegramUserId) {
        String url = telegramBot.getEnvBaseUrl() + ":8080/api/usuarios/telegram/" + telegramUserId;

        return webClient.get()
                .uri(url)
                .retrieve()
                .onStatus(status -> status.value() == 404, clientResponse -> {
                    return Mono.just(new UsuarioNotFoundException());
                })
                .bodyToMono(Usuario.class);
    }
}