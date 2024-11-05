package tacs.grupo_4.telegramBot.handlers;

import org.springframework.context.annotation.Profile;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import tacs.grupo_4.entities.Usuario;
import tacs.grupo_4.entities.Evento;
import tacs.grupo_4.entities.Ticket;
import tacs.grupo_4.entities.Ubicacion;
import tacs.grupo_4.entities.Sector;
import tacs.grupo_4.exceptions.UsuarioNotFoundException;
import tacs.grupo_4.telegramBot.ImpresoraJSON;
import tacs.grupo_4.telegramBot.TelegramBot;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Profile("bot")
@Component
public class EventoHandler {
    private final WebClient webClient;
    private final TelegramBot telegramBot;
    private final UsuarioHandler usuarioHandler;

    public EventoHandler(WebClient.Builder webClientBuilder, TelegramBot telegramBot, UsuarioHandler usuarioHandler) {
        this.webClient = webClientBuilder.build();
        this.telegramBot = telegramBot;
        this.usuarioHandler = usuarioHandler;
    }

    public String crearEvento(String[] mensaje, String chatId, Long telegramUserId) {
        // Limito las funcionalidades desde el bot: no se puede tener multiples sectores
        if (mensaje.length != 7) {
            return "crearEvento <nombre>,<aaaa-mm-ddThora:minuto:segundo>,<descripcionEvento>,<nombreUbicacion>,<direccion>,<capacidad>,<precio>";
        }
        Usuario usuario = verificarUsusario(chatId, telegramUserId);
        if (usuario != null) {

            String url = telegramBot.getEnvBaseUrl() + ":8080/api/eventos";

            Ubicacion ubicacion = Ubicacion.builder()
                    .id(UUID.randomUUID())
                    .nombre(mensaje[3])
                    .direccion(mensaje[4])
                    .build();
            Sector sector = Sector.builder()
                    .id(UUID.randomUUID())
                    .capacidad(Long.valueOf(mensaje[5]))
                    .precio(Double.valueOf(mensaje[6]))
                    .build();
            List<Sector> sectores = new ArrayList<>();
            sectores.add(sector);
            Evento evento = Evento.builder()
                    .id(UUID.randomUUID())
                    .nombre(mensaje[0])
                    .fecha(LocalDateTime.parse(mensaje[1]))
                    .descripcion(mensaje[2])
                    .estaConfirmado(false) // Se activa cuando se confirma y se generan los asientos
                    .ubicacion(ubicacion)
                    .sectores(sectores)
                    .usuario(usuario.getId())
                    .estaActivo(true)
                    .build();

            Mono<Evento> responseMono = webClient.post()
                    .uri(url)
                    .bodyValue(evento)
                    .retrieve()
                    .onStatus(status -> status.value() == 409, clientResponse -> {
                        return Mono.just(new RuntimeException("El evento ya existe."));
                    })
                    .bodyToMono(Evento.class);

            responseMono.subscribe(
                    response -> telegramBot.enviarMensaje(chatId,
                            "Evento creado exitosamente: \n" + ImpresoraJSON.imprimir(response)),
                    error -> telegramBot.enviarMensaje(chatId,
                            "Hubo un error: " + error.getMessage())
            );
        }

        return "";
    }

    public String misEventos(String[] mensaje, String chatId, Long telegramUserId) {
        if (mensaje.length != 0) {
            return "No se esperaban parámetros";
        }
        Usuario usuario = verificarUsusario(chatId, telegramUserId);
        if (usuario != null) {

            String url = telegramBot.getEnvBaseUrl() + ":8080/api/usuarios/" + usuario.getId() + "/eventos";
            Mono<List<Evento>> responseMono = webClient.get()
                    .uri(url)
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<List<Evento>>() {
                    });

            responseMono.subscribe(
                    response -> telegramBot.enviarMensaje(chatId,
                            "Tus eventos son: \n\n" + ImpresoraJSON.imprimirEventos(response)),
                    error -> telegramBot.enviarMensaje(chatId,
                            "Hubo un error: " + error.getMessage())
            );
        }

        return "";
    }

    public String eventos(String[] mensaje, String chatId) {
        if (mensaje.length != 0) {
            return "No se esperaban parámetros";
        }

        String url = telegramBot.getEnvBaseUrl() + ":8080/api/eventos";
        Mono<List<Evento>> responseMono = webClient.get()
                .uri(url)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<Evento>>() {
                });

        responseMono.subscribe(
                response -> telegramBot.enviarMensaje(chatId,
                        "Los eventos son: \n\n" + ImpresoraJSON.imprimirEventos(response)),
                error -> telegramBot.enviarMensaje(chatId,
                        "Hubo un error: " + error.getMessage())
        );

        return "";
    }

    public String reservarAsientoEnSector(String[] mensaje, String chatId, Long telegramUserId) {
        if (mensaje.length != 2) {
            return "reservar <eventoId>,<sectorId>";
        } // No parece cómodo utilizar los id
        Mono<Usuario> usuarioAsync = usuarioHandler.findByTelegramId(telegramUserId);
        Usuario usuario;
        try {
            usuario = usuarioAsync.block(); //Espero de forma sincronica
        } catch (UsuarioNotFoundException e) {
            telegramBot.enviarMensaje(chatId, "Acceso denegado");
            return "";
        }

        String url = telegramBot.getEnvBaseUrl() + ":8080/api/eventos/" + mensaje[0] + "/sector/" + mensaje[1] + "/usuario/" + usuario.getId();
        Mono<Ticket> responseMono = webClient.put()
                .uri(url)
                .retrieve()
                .onStatus(status -> status.value() == 412, clientResponse -> {
                    return Mono.just(new RuntimeException("No hay asientos disponibles en ese evento o sector."));
                })
                .onStatus(status -> status.value() == 500, clientResponse -> {
                    return Mono.just(new RuntimeException("Parametros inválidos"));
                })
                .bodyToMono(Ticket.class);

        responseMono.subscribe(
                response -> telegramBot.enviarMensaje(chatId,
                        "Asiento reservado exitosamente: \n" + ImpresoraJSON.imprimir(response)),
                error -> telegramBot.enviarMensaje(chatId,
                        "Hubo un error: " + error.getMessage())
        );

        return "";
    }

    public String confirmarEvento(String[] mensaje, String chatId, Long telegramUserId) {
        if (mensaje.length != 1) {
            return "confirmarEvento <eventoId>";
        } // No parece cómodo utilizar los id
        Usuario usuario = verificarUsusario(chatId, telegramUserId);
        if (usuario != null) {
            String url = telegramBot.getEnvBaseUrl() + ":8080/api/eventos/" + mensaje[0] + "/confirmar/" + usuario.getId();
            Mono<Evento> responseMono = webClient.put()
                    .uri(url)
                    .retrieve()
                    .bodyToMono(Evento.class);

            responseMono.subscribe(
                    response -> telegramBot.enviarMensaje(chatId,
                            "Se ha confirmado el evento: \n" + ImpresoraJSON.imprimir(response)),
                    error -> telegramBot.enviarMensaje(chatId,
                            "Hubo un error: " + error.getMessage())
            );
        }

        return "";
    }

    public String cancelarEvento(String[] parametros, String chatId, Long telegramUserId) {
        if (parametros.length != 1) {
            return "cancelarEvento <eventoId>";
        }
        Usuario usuario = verificarUsusario(chatId, telegramUserId);
        if (usuario != null) {
            String eventoId = parametros[0];
            String url = telegramBot.getEnvBaseUrl() + ":8080/api/eventos/" + eventoId + "/usuario/" + usuario.getId();
            Mono<Void> responseMono = webClient.put()
                    .uri(url)
                    .retrieve()
                    .onStatus(status -> status.value() == 404, clientResponse -> {
                        return Mono.just(new RuntimeException("Evento no encontrado."));
                    })
                    .onStatus(status -> status.value() == 403, clientResponse -> {
                        return Mono.just(new RuntimeException("No tienes permisos para cancelar este evento."));
                    }).onStatus(status -> status.value() == 500, clientResponse -> {
                        return Mono.just(new RuntimeException("Evento no encontrado."));
                    })
                    .bodyToMono(Void.class);
            responseMono.subscribe(
                    unused -> telegramBot.enviarMensaje(chatId, "Evento cancelado exitosamente."),
                    error -> telegramBot.enviarMensaje(chatId, "Hubo un error: " + error.getMessage())
            );
        }
        return "";
    }

    public String eliminarEvento(String[] parametros, String chatId, Long telegramUserId) {
        if (parametros.length != 1) {
            return "cancelarEvento <eventoId>";
        }
        Usuario usuario = verificarUsusario(chatId, telegramUserId);
        if (usuario != null) {
            String eventoId = parametros[0];
            String url = telegramBot.getEnvBaseUrl() + ":8080/api/eventos/" + eventoId + "/usuario/" + usuario.getId();
            Mono<Void> responseMono = webClient.delete()
                    .uri(url)
                    .retrieve()
                    .onStatus(status -> status.value() == 404, clientResponse -> {
                        return Mono.just(new RuntimeException("Evento no encontrado."));
                    })
                    .onStatus(status -> status.value() == 403, clientResponse -> {
                        return Mono.just(new RuntimeException("No tienes permisos para cancelar este evento."));
                    }).onStatus(status -> status.value() == 500, clientResponse -> {
                        return Mono.just(new RuntimeException("Evento no encontrado."));
                    })
                    .bodyToMono(Void.class);
            responseMono.subscribe(
                    unused -> telegramBot.enviarMensaje(chatId, "Evento cancelado exitosamente."),
                    error -> telegramBot.enviarMensaje(chatId, "Hubo un error: " + error.getMessage())
            );
        }
        return "";
    }


    private Usuario verificarUsusario(String chatId, Long telegramUserId) {
        System.out.println("VERIFICANDO USUARIO");
        Mono<Usuario> usuarioAsync = usuarioHandler.findByTelegramId(telegramUserId);
        Usuario usuario;
        System.out.println("VERIFICANDO USUARIO");
        try {
            usuario = usuarioAsync.block(); //Espero de forma sincronica
        } catch (UsuarioNotFoundException e) {
            telegramBot.enviarMensaje(chatId, "Acceso denegado");
            return null;
        }
        return usuario;
    }

}
