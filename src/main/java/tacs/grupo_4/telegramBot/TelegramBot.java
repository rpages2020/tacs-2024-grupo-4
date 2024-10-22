package tacs.grupo_4.telegramBot;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import tacs.grupo_4.telegramBot.handlers.EventoHandler;
import tacs.grupo_4.telegramBot.handlers.UsuarioHandler;

@Profile("bot")
@Service
public class TelegramBot extends TelegramLongPollingBot {
    private final UsuarioHandler usuarioHandler;
    private final EventoHandler eventoHandler;
    @Autowired
    public TelegramBot(@Value("${telegram.bot.token}") String botToken,
                       @Lazy UsuarioHandler usuarioHandler,
                       @Lazy EventoHandler eventoHandler) {
        super(botToken);  // non-deprecated constructor
        this.usuarioHandler = usuarioHandler;
        this.eventoHandler = eventoHandler;
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

            int primerEspacioIndex = messageText.indexOf(' ');
            String comando;
            String[] parametros;
            if (primerEspacioIndex == -1) {
                comando = messageText.toLowerCase();
                parametros = new String[0];
            } else {
                comando = messageText.substring(0, primerEspacioIndex).toLowerCase();
                String parametrosString = messageText.substring(primerEspacioIndex + 1);
                parametros = parametrosString.split(",");
            }
            String respuesta = switch (comando) {
                case "confirmarevento"  ->      eventoHandler.confirmarEvento(parametros, chatId, telegramUserId);
                case "crearusuario"     ->      usuarioHandler.crearUsuario(parametros, chatId, telegramUserId);
                case "crearevento"      ->      eventoHandler.crearEvento(parametros, chatId, telegramUserId);
                case "ejemplos"         ->      ejemplos(chatId);
                case "eventos"          ->      eventoHandler.eventos(parametros, chatId);
                case "help"             ->      helpMensaje;
                case "hola"             ->      "chau";
                case "miseventos"       ->      eventoHandler.misEventos(parametros, chatId, telegramUserId);
                case "reservar"         ->      eventoHandler.reservarAsientoEnSector(parametros, chatId, telegramUserId);
                case "whoami"           ->      usuarioHandler.whoami(parametros, chatId, telegramUserId);
                default                 ->      bienvenida(nombre);
            };
            if (!respuesta.isEmpty()) {
                enviarMensaje(chatId, respuesta);
            }
        }
    }
    private final String helpMensaje =
            """
                    Comandos disponibles:
                    • confirmarEvento <eventoId>
                    • crearEvento <nombre>,<aaaa-mm-ddThora:minuto:segundo>,<descripcionEvento>,<nombreUbicacion>,<direccion>,<capacidad>,<precio>"
                    • crearUsuario <nombre>,<email>,<dni>
                    • ejemplos
                    • eventos
                    • hola
                    • misEventos
                    • reservar <eventoId>,<sectorId>
                    • whoami
            """;
    private String bienvenida(String nombre) {
        return "¡Hola " + nombre + "! Soy el TicketBot."
                + " Puedes utilizar 'help' para ver las operaciones disponibles";
    }

    public String ejemplos(String chatId) {
        String mensaje;
        mensaje = "crearEvento Eventardo,2025-09-20T00:00:00,Festival Azul,La Rural,Avenida Siempreviva,1000,150";
        enviarMensaje(chatId, mensaje);
        mensaje = "crearUsuario Juan Pablo,juan@juan.com,12345678";
        enviarMensaje(chatId, mensaje);
        mensaje = "hola";
        enviarMensaje(chatId, mensaje);
        mensaje = "misEventos";
        enviarMensaje(chatId, mensaje);
        mensaje = "whoami";
        enviarMensaje(chatId, mensaje);
        return "";
    }
    public void enviarMensaje(String chatId, String texto) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText(texto);

        try {
            execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
    public String getEnvBaseUrl() {
        return "http://grupo_4_api:8080";
    }

}
