package tacs.grupo_4.telegramBot.handlers;

import org.springframework.context.annotation.Profile;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import tacs.grupo_4.entities.Estadisticas;
import tacs.grupo_4.entities.Usuario;
import tacs.grupo_4.telegramBot.ImpresoraJSON;
import tacs.grupo_4.telegramBot.TelegramBot;

@Profile("bot")
@Component
public class AdminHandler {

    private final WebClient webClient;
    private final TelegramBot telegramBot;
    private final UsuarioHandler usuarioHandler;

    public AdminHandler(WebClient.Builder webClientBuilder, TelegramBot telegramBot, UsuarioHandler usuarioHandler) {
        this.webClient = webClientBuilder.build();
        this.telegramBot = telegramBot;
        this.usuarioHandler = usuarioHandler;
    }

    public String estadisticas (String[] mensaje, String chatId, Long telegramUserId) {
        if (mensaje.length != 0) {
            return "No se esperaban parámetros";
        }
        String url = telegramBot.getEnvBaseUrl() + ":8080/api/estadisticas";
        Usuario usuario = usuarioHandler.verificarAdmin(chatId, telegramUserId);
        if (usuario != null) {
            Mono<Estadisticas> responseMono = webClient.get()
                    .uri(url)
                    .retrieve()
                    .bodyToMono(Estadisticas.class);

            responseMono.subscribe(
                    response -> telegramBot.enviarMensaje(chatId,
                            "Estadísticas del sitio: \n" + ImpresoraJSON.imprimir(response)),
                    error -> telegramBot.enviarMensaje(chatId,
                            "Hubo un error: " + error.getMessage())
            );
        }
        return "";
    }

    public String ticketsPorFecha(String[] mensaje, String chatId, Long telegramUserId) {
        if (mensaje.length < 1 || mensaje.length > 2) {
            return "ticketsPorFecha <dd-mm-aa> \n ticketsPorFecha  <dd-mm-aa>,<dd-mm-aa>";
        }
        if (mensaje.length == 1) {
        String fecha = mensaje[0];
        //verificar admin o antes
        String url = telegramBot.getEnvBaseUrl() + ":8080/api/reservas/fecha/" + fecha;
        Mono<Long> responseMono = webClient.get()
                .uri(url)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<Long>() {
                });

        responseMono.subscribe(
                response -> telegramBot.enviarMensaje(chatId,
                        "La cantidad de Tickets vendidos el " + fecha + " es: " + response),
                error -> telegramBot.enviarMensaje(chatId,
                        "Hubo un error: " + error.getMessage())
        );
        } else {
            String fecha1 = mensaje[0];
            String fecha2 = mensaje[1];
            //verificar admin o antes
            String url = telegramBot.getEnvBaseUrl() + ":8080/api/reservas/entreFechas/" + fecha1 + "/" + fecha2;
            Mono<Long> responseMono = webClient.get()
                    .uri(url)
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<Long>() {
                    });

            responseMono.subscribe(
                    response -> telegramBot.enviarMensaje(chatId,
                            "La cantidad de tickes que se vendieron entre el " + fecha1 + " y el " + fecha2 + " es: " + response),
                    error -> telegramBot.enviarMensaje(chatId,
                            "Hubo un error: " + error.getMessage())
            );
        }

        return "";
    }

    public String usuariosPorFecha(String[] mensaje, String chatId, Long telegramUserId) {
        if (mensaje.length < 1 || mensaje.length > 2) {
            return "altasUsuariosPorFecha <dd-mm-aa> \n altasUsuariosPorFecha <dd-mm-aa>,<dd-mm-aa>";
        }
        if (mensaje.length == 1) {
            String fecha = mensaje[0];
            //verificar admin o antes
            String url = telegramBot.getEnvBaseUrl() + ":8080/api/usuarios/fechaAlta/" + fecha;
            Mono<Long> responseMono = webClient.get()
                    .uri(url)
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<Long>() {
                    });

            responseMono.subscribe(
                    response -> telegramBot.enviarMensaje(chatId,
                            "La cantidad de ususarios que se dieron de alta el " + fecha + " es: " + response),
                    error -> telegramBot.enviarMensaje(chatId,
                            "Hubo un error: " + error.getMessage())
            );
        } else {
            String fecha1 = mensaje[0];
            String fecha2 = mensaje[1];
            //verificar admin o antes
            String url = telegramBot.getEnvBaseUrl() + ":8080/api/usuarios/entreFechas/" + fecha1 + "/" + fecha2;
            Mono<Long> responseMono = webClient.get()
                    .uri(url)
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<Long>() {
                    });

            responseMono.subscribe(
                    response -> telegramBot.enviarMensaje(chatId,
                            "La cantidad de ususarios que se dieron de alta entre " +  fecha1 + " y el " + fecha2 + " es: " + response),
                    error -> telegramBot.enviarMensaje(chatId,
                            "Hubo un error: " + error.getMessage())
            );
        }
        return "";
    }
}