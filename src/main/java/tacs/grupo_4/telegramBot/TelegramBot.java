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

import java.util.UUID;

@Service
public class TelegramBot extends TelegramLongPollingBot {

    private final WebClient.Builder webClientBuilder;

    @Autowired
    public TelegramBot(WebClient.Builder webClientBuilder,
                       @Value("${telegram.bot.token}") String botToken) {
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

            Usuario usuario = Usuario.builder()
                    .id(UUID.randomUUID())
                    .nombre("Juan Perez")
                    .email("juan.perez@example.com")
                    .build();

            // Ejemplo de llamada asíncrona a una API expuesta en tu aplicación
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
        }
    }
}
