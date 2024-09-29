package tacs.grupo_4.telegramBot.handlers;

import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import tacs.grupo_4.entities.Evento;
import tacs.grupo_4.entities.Sector;
import tacs.grupo_4.entities.Ubicacion;
import tacs.grupo_4.entities.Usuario;
import tacs.grupo_4.telegramBot.TelegramBot;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

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
        Mono<Usuario> usuarioAsync = usuarioHandler.findByTelegramId(telegramUserId);
        Usuario usuario = usuarioAsync.block();//Espero de forma sincronica
        if(usuario == null) {
            telegramBot.enviarMensaje(chatId, "Acceso denegado");
            return "";
        }

        String url = "http://localhost:8080/api/eventos";

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
                .estaActivo(true)
                .ubicacion(ubicacion)
                .sectores(sectores)
                .usuario(usuario.getId())
                .build();

        Mono<String> responseMono = webClient.post()
                .uri(url)
                .bodyValue(evento)
                .retrieve()
                .onStatus(status -> status.value() == 409, clientResponse -> {
                    return Mono.just(new RuntimeException("El evento ya existe."));
                })
                .bodyToMono(String.class);

        responseMono.subscribe(
                response -> telegramBot.enviarMensaje(chatId,
                        "Evento creado exitosamente: \n" + response),
                error -> telegramBot.enviarMensaje(chatId,
                        "Hubo un error: " + error.getMessage())
        );

        return "";
    }
}
