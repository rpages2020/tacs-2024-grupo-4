package tacs.grupo_4.telegramBot;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import reactor.core.publisher.Mono;
import tacs.grupo_4.entities.Usuario;
import tacs.grupo_4.services.UsuarioService;

import java.util.UUID;

@Service
public class TelegramBot extends TelegramLongPollingBot {

    private final WebClient.Builder webClientBuilder;

    @Autowired
    public TelegramBot(WebClient.Builder webClientBuilder,
                       @Value("${telegram.bot.token}") String botToken, UsuarioService usuarioService) {
        super(botToken);  // non-deprecated constructor
        this.webClientBuilder = webClientBuilder;
    }

    @Override
    public String getBotUsername() {
        return "@hyperdestroyerbot";
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            String messageText = update.getMessage().getText();
            String chatId = update.getMessage().getChatId().toString();
            Long telegramUserId = update.getMessage().getFrom().getId();
            String nombre = update.getMessage().getFrom().getFirstName();


            String[] mensajePartido = messageText.split(" ", 50);
            String comando = mensajePartido[0];
            String respuesta = switch(comando) {
                case "hola" -> "chau";
                case "help" -> helpMensaje;
                case "whoami" -> whoamiHandler(mensajePartido, chatId, telegramUserId);
                case "crearUsuario" -> crearUsuarioHandler(mensajePartido, chatId, telegramUserId);
                default -> "¡Hola " + nombre + "! Soy el TicketBot. Puedes utilizar 'help' para ver las operaciones disponibles";
            };
            if (!respuesta.isEmpty()) {
                SendMessage message = new SendMessage();
                message.setChatId(chatId);
                message.setText(respuesta);
                try {
                    execute(message);
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }
            }


                /*
            Usuario usuario = Usuario.builder()
                    .id(UUID.randomUUID())
                    .nombre("Juan Perez")
                    .email("juan.perez@example.com")
                    .build();


            String url = "http://localhost:8080/api/usuarios";

            WebClient webClient = webClientBuilder.build();
            Mono<String> responseMono = webClient.post()
                    .uri(url)
                    .bodyValue(usuario)
                    .retrieve()
                    .bodyToMono(String.class);

            // Procesa la respuesta de manera asíncrona
            responseMono.subscribe(response -> {
                SendMessage message = new SendMessage();
                message.setChatId(chatId);
                message.setText("Respuesta de la API: " + response);
                try {
                    execute(message);
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }
            });
                 */
        }
    }
    String helpMensaje =
            """
                    Comandos disponibles:
                    • whoami
                    • hola
            """;
    private String whoamiHandler(String[] mensaje, String chatId, Long telegramUserId) {
        if (mensaje.length != 1) {
           return "No se esperaban parámetros";
        }
        String url = "http://localhost:8080/api/usuarios/telegram/" + telegramUserId.toString();

        WebClient webClient = webClientBuilder.build();
        Mono<String> responseMono = webClient.get()
                .uri(url)
                .retrieve()
                .bodyToMono(String.class);
        responseMono.subscribe(response -> {
            SendMessage message = new SendMessage();
            message.setChatId(chatId);
            message.setText("Vos sos: " + response);
            try {
                execute(message);
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        });
        return "";
    }
    private String crearUsuarioHandler(String[] mensaje, String chatId, Long telegramUserId) {
        if (mensaje.length != 3) {
            return "crearUsuario <'nombre'> <email>";
        }
        String url = "http://localhost:8080/api/usuarios";

        Usuario usuario = Usuario.builder()
                .id(UUID.randomUUID())
                .nombre(mensaje[1].replace("'", ""))
                .email(mensaje[2])
                .telegramUserId(telegramUserId)
                .build();

        WebClient webClient = webClientBuilder.build();
        Mono<String> responseMono = webClient.post()
                .uri(url)
                .bodyValue(usuario)
                .retrieve()
                .bodyToMono(String.class);

        responseMono.subscribe(response -> {
            SendMessage message = new SendMessage();
            message.setChatId(chatId);
            message.setText("Usuario creado: " + response);
            try {
                execute(message);
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        });
        return "";
    }
}
