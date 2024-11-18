package tacs.grupo_4.telegramBot.handlers;

import org.springframework.context.annotation.Profile;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import tacs.grupo_4.entities.Ticket;
import tacs.grupo_4.entities.Usuario;
import tacs.grupo_4.exceptions.UsuarioNotFoundException;
import tacs.grupo_4.telegramBot.ImpresoraJSON;
import tacs.grupo_4.telegramBot.TelegramBot;

import java.util.List;
import java.util.UUID;

@Profile("bot")
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

    public Mono<Usuario> findByDni(int dni) {
        String url = telegramBot.getEnvBaseUrl() + ":8080/api/usuarios/dni/" + dni;

        return webClient.get()
                .uri(url)
                .retrieve()
                .onStatus(status -> status.value() == 404, clientResponse -> {
                    return Mono.just(new UsuarioNotFoundException());
                })
                .bodyToMono(Usuario.class);
    }

    public Usuario verificarAdmin(String chatId, Long telegramUserId) {
        Usuario usuario = verificarUsusario(chatId, telegramUserId);
        if (usuario == null || !usuario.isModoAdmin()) {
            telegramBot.enviarMensaje(chatId, "Acceso denegado - No tiene permisos de administrador");
            return null;
        }
        return usuario;
    }


    public String misReservas(String[] mensaje, String chatId, Long telegramUserId) {
        if (mensaje.length != 0) {
            return "No se esperaban parámetros";
        }
        Usuario usuario = verificarUsusario(chatId, telegramUserId);
        if (usuario != null) {
            String url = telegramBot.getEnvBaseUrl() + ":8080/api/usuarios/" + usuario.getId() + "/tickets";
            Mono<List<Ticket>> responseMono = webClient.get()
                    .uri(url)
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<List<Ticket>>() {
                    });

            responseMono.subscribe(
                    response -> telegramBot.enviarMensaje(chatId,
                            "Tus reservas son: \n\n" + ImpresoraJSON.imprimirTickets(response)),
                    error -> telegramBot.enviarMensaje(chatId,
                            "Hubo un error: " + error.getMessage())
            );
        }
        return "";

    }
    private Usuario verificarUsusario(String chatId, Long telegramUserId) {
        Mono<Usuario> usuarioAsync = this.findByTelegramId(telegramUserId);
        Usuario usuario;
        try {
            usuario = usuarioAsync.block(); //Espero de forma sincronica
        } catch (UsuarioNotFoundException e) {
            telegramBot.enviarMensaje(chatId, "Acceso denegado");
            return null;
        }
        return usuario;
    }

//    public boolean verificarAdmin(Usuario usuario, String chatId) {
//        String url = telegramBot.getEnvBaseUrl() + ":8080/api/usuarios/esAdmin/" + usuario.getId();
//        Mono<Boolean> responseMono = webClient.get()
//                .uri(url)
//                .retrieve()
//                .bodyToMono(new ParameterizedTypeReference<Boolean>() {
//                });
//        responseMono.subscribe(
//                _ -> telegramBot.enviarMensaje(chatId, ""),
//                error -> telegramBot.enviarMensaje(chatId,
//                        "Hubo un error: " + error.getMessage())
//        );
//     return Boolean.TRUE.equals(responseMono.block());
//    }

    public String activarModoAdmin(String[] mensaje, String chatId, Long telegramUserId) {
        if (mensaje.length != 0 && mensaje.length != 1) {
            return "No se esperaban parámetros";
        }
        Usuario usuario = verificarUsusario(chatId, telegramUserId);
        if (mensaje.length == 1) {  // si pasa dni y es admin, modificar ese usuario
            if (verificarAdmin(chatId, telegramUserId) == null) {
                return "";
            };
            int dni = Integer.parseInt(mensaje[0]);
            usuario = findByDni(dni).block();
        }
        if (usuario != null) {
            UUID usuarioId = usuario.getId();
            String url = telegramBot.getEnvBaseUrl() + ":8080/api/usuarios/" + usuarioId + "/adminModeOn";
            Mono<Boolean> responseMono = webClient.put()
                    .uri(url)
                    .retrieve()
                    .onStatus(status -> status.value() != 200, clientResponse -> {
                        return Mono.just(new RuntimeException("Error"));
                    })
                    .bodyToMono(Boolean.class);
            responseMono.subscribe(
                    response -> telegramBot.enviarMensaje(chatId, "Se ha activado el modo admin"),
                    error -> telegramBot.enviarMensaje(chatId, "Hubo un error: " + error.getMessage())
            );
        }
        return "";
    }

    public String descartivarModoAdmin(String[] mensaje, String chatId, Long telegramUserId) {
        if (mensaje.length != 0 && mensaje.length != 1) {
            return "No se esperaban parámetros";
        }
        Usuario usuario = verificarUsusario(chatId, telegramUserId);
        if (mensaje.length == 1) {  // si pasa dni y es admin, modificar ese usuario
            if (verificarAdmin(chatId, telegramUserId) == null) {
                return "";
            };
            int dni = Integer.parseInt(mensaje[0]);
            usuario = findByDni(dni).block();
        }
        if (usuario != null) {
            UUID usuarioId = usuario.getId();
            String url = telegramBot.getEnvBaseUrl() + ":8080/api/usuarios/" + usuarioId + "/adminModeOff";
            Mono<Boolean> responseMono = webClient.put()
                    .uri(url)
                    .retrieve()
                    .onStatus(status -> status.value() != 200, clientResponse -> {
                        return Mono.just(new RuntimeException("Error"));
                    })
                    .bodyToMono(Boolean.class);
            responseMono.subscribe(
                    response -> telegramBot.enviarMensaje(chatId, "Se ha desactivado el modo admin"),
                    error -> telegramBot.enviarMensaje(chatId,
                            "Hubo un error: " + error.getMessage())
            );
        }
        return "";
    }
}