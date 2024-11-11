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
        // Comprobar que el mensaje tiene al menos los datos básicos + un sector
        if (mensaje.length < 7 || (mensaje.length - 5) % 3 != 0) {
            return "Lo sentimos, formato incorrecto! \nUtiliza:\ncrearEvento <nombre>,<aaaa-mm-ddThora:minuto:segundo>,<descripcionEvento>,<nombreUbicacion>,<direccion>,<nombreSector1>,<capacidad1>,<precio1>,<nombreSector2>,<capacidad2>,<precio2>,...";

        }

        Usuario usuario = verificarUsusario(chatId, telegramUserId);
        if (usuario != null) {
            String url = telegramBot.getEnvBaseUrl() + ":8080/api/eventos";

            Ubicacion ubicacion = Ubicacion.builder()
                    .id(UUID.randomUUID())
                    .nombre(mensaje[3])
                    .direccion(mensaje[4])
                    .build();

            // Crear la lista de sectores
            List<Sector> sectores = new ArrayList<>();
            for (int i = 5; i < mensaje.length; i += 3) {
                Sector sector = Sector.builder()
                        .id(UUID.randomUUID())
                        .nombre(mensaje[i])                      // Nombre del sector
                        .capacidadTotal(Integer.valueOf(mensaje[i + 1])) // CapacidadTotal
                        .precio(Double.valueOf(mensaje[i + 2]))  // Precio del ticket
                        .reservas(0) //ventas
                        .build();
                sectores.add(sector);
            }

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

            // Enviar solicitud de creación de evento
            Mono<Evento> responseMono = webClient.post()
                    .uri(url)
                    .bodyValue(evento)
                    .retrieve()
                    .onStatus(status -> status.value() == 409, clientResponse -> {
                        return Mono.just(new RuntimeException("El evento ya existe."));
                    })
                    .bodyToMono(Evento.class);

            // Suscribirse para enviar mensajes de confirmación o error
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
                            "Tus eventos son: \n\n" + ImpresoraJSON.imprimirEventosYEstadisticas(response)),
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
            return "reservar <eventoId>,<nombreSector>";
        }
        Usuario usuario = verificarUsusario(chatId, telegramUserId);
        if (usuario != null) {

            String url = telegramBot.getEnvBaseUrl() + ":8080/api/eventos/" + mensaje[0] + "/sector/" + mensaje[1] + "/usuario/" + usuario.getId();
            Mono<Ticket> responseMono = webClient.put()
                    .uri(url)
                    .retrieve()
                    .onStatus(status -> status.value() == 412, clientResponse -> {
                        return Mono.just(new RuntimeException("No hay asientos disponibles en ese evento o sector."));
                    }).onStatus(status -> status.value() == 409, clientResponse -> {
                        return Mono.just(new RuntimeException("El evento ya no esta disponible"));
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
        }
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
            Mono<String> responseMono = webClient.put()
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
                    .bodyToMono(String.class);
            try {   // Ejecución síncrona
                responseMono.block();
                telegramBot.enviarMensaje(chatId, "Evento cancelado exitosamente.");
            } catch (Exception error) {
                telegramBot.enviarMensaje(chatId, "Hubo un error: " + error.getMessage());
            }
        }
        return "";
    }

    public String eliminarEvento(String[] parametros, String chatId, Long telegramUserId) {
        if (parametros.length != 1) {
            return "eliminarEvento <eventoId>";
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
                    response -> telegramBot.enviarMensaje(chatId, "Evento eliminado exitosamente."),
                    error -> telegramBot.enviarMensaje(chatId, "Hubo un error: " + error.getMessage())
            );
        }
        return "";
    }


    public Usuario verificarUsusario(String chatId, Long telegramUserId) {
        Mono<Usuario> usuarioAsync = usuarioHandler.findByTelegramId(telegramUserId);
        Usuario usuario;
        try {
            usuario = usuarioAsync.block(); //Espero de forma sincronica
        } catch (UsuarioNotFoundException e) {
            telegramBot.enviarMensaje(chatId, "Acceso denegado");
            return null;
        }
        return usuario;
    }

    public String cerrarEvento(String[] parametros, String chatId, Long telegramUserId) {
        cancelarEvento(parametros, chatId, telegramUserId);

        String url = telegramBot.getEnvBaseUrl() + ":8080/api/eventos/" + parametros[0];

        Mono<Evento> responseMono = webClient.get()
                .uri(url)
                .retrieve()
                .onStatus(status -> status.value() == 404, clientResponse -> {
                    return Mono.just(new RuntimeException("Evento no encontrado."));
                })
                .onStatus(status -> status.value() == 403, clientResponse -> {
                    return Mono.just(new RuntimeException("No tienes permisos para cancelar este evento."));
                })
                .onStatus(status -> status.value() == 500, clientResponse -> {
                    return Mono.just(new RuntimeException("Evento no encontrado."));
                })
                .bodyToMono(Evento.class);

        responseMono.subscribe(
                response -> telegramBot.enviarMensaje(chatId, "Evento cerrado: " + ImpresoraJSON.imprimirEventoYEstadistica(response)),
                error -> telegramBot.enviarMensaje(chatId, "Hubo un error: " + error.getMessage())
        );
        return "";
    }

}
