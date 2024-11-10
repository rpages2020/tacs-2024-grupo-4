package tacs.grupo_4.telegramBot.handlers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import tacs.grupo_4.entities.Evento;
import tacs.grupo_4.entities.Sector;
import tacs.grupo_4.entities.Ubicacion;
import tacs.grupo_4.entities.Usuario;
import tacs.grupo_4.exceptions.UsuarioNotFoundException;
import tacs.grupo_4.telegramBot.TelegramBot;
import tacs.grupo_4.telegramBot.handlers.UsuarioHandler;

import java.time.LocalDateTime;
import java.util.UUID;
import java.util.List;

import static org.mockito.Mockito.*;

public class EventoHandlerTest {

    private EventoHandler eventoHandler;
    private WebClient webClient;
    private TelegramBot telegramBot;
    private UsuarioHandler usuarioHandler;

    @BeforeEach
    void setUp() {
        webClient = Mockito.mock(WebClient.class, RETURNS_DEEP_STUBS);
        telegramBot = Mockito.mock(TelegramBot.class);
        usuarioHandler = Mockito.mock(UsuarioHandler.class);
        eventoHandler = new EventoHandler(WebClient.builder(), telegramBot, usuarioHandler);
    }

    @Test
    void crearEvento_Success() {
        String[] mensaje = {"eventoTest", "2024-10-10T10:00:00", "eventoDesc", "ubicacionTest", "direccionTest", "100", "50"};
        String chatId = "123";
        Long telegramUserId = 1L;

        Usuario mockUsuario = new Usuario();
        mockUsuario.setId(UUID.randomUUID());

        when(usuarioHandler.findByTelegramId(telegramUserId)).thenReturn(Mono.just(mockUsuario));

        WebClient.ResponseSpec responseSpec = mock(WebClient.ResponseSpec.class);
        when(webClient.post().uri(anyString()).bodyValue(any(Evento.class)).retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(Evento.class)).thenReturn(Mono.just(new Evento()));

        String result = eventoHandler.crearEvento(mensaje, chatId, telegramUserId);
        verify(telegramBot).enviarMensaje(eq(chatId), contains("Evento creado exitosamente"));

        assert result.isEmpty();
    }

    @Test
    void crearEvento_Failure_UserNotFound() {
        String[] mensaje = {"eventoTest", "2024-10-10T10:00:00", "eventoDesc", "ubicacionTest", "direccionTest", "100", "50"};
        String chatId = "123";
        Long telegramUserId = 1L;

        when(usuarioHandler.findByTelegramId(telegramUserId)).thenThrow(new UsuarioNotFoundException());

        String result = eventoHandler.crearEvento(mensaje, chatId, telegramUserId);
        verify(telegramBot).enviarMensaje(eq(chatId), eq("Acceso denegado"));

        assert result.isEmpty();
    }

    @Test
    void misEventos_Success() {
        String[] mensaje = {};
        String chatId = "123";
        Long telegramUserId = 1L;

        Usuario mockUsuario = new Usuario();
        mockUsuario.setId(UUID.randomUUID());

        when(usuarioHandler.findByTelegramId(telegramUserId)).thenReturn(Mono.just(mockUsuario));

        WebClient.ResponseSpec responseSpec = mock(WebClient.ResponseSpec.class);
        when(webClient.get().uri(anyString()).retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(any(ParameterizedTypeReference.class))).thenReturn(Mono.just(List.of(new Evento())));

        String result = eventoHandler.misEventos(mensaje, chatId, telegramUserId);
        verify(telegramBot).enviarMensaje(eq(chatId), contains("Tus eventos son"));

        assert result.isEmpty();
    }

    @Test
    void misEventos_Failure_UserNotFound() {
        String[] mensaje = {};
        String chatId = "123";
        Long telegramUserId = 1L;

        when(usuarioHandler.findByTelegramId(telegramUserId)).thenThrow(new UsuarioNotFoundException());

        String result = eventoHandler.misEventos(mensaje, chatId, telegramUserId);
        verify(telegramBot).enviarMensaje(eq(chatId), eq("Acceso denegado"));

        assert result.isEmpty();
    }

    @Test
    void eventos_Success() {
        String[] mensaje = {};
        String chatId = "123";

        WebClient.ResponseSpec responseSpec = mock(WebClient.ResponseSpec.class);
        when(webClient.get().uri(anyString()).retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(any(ParameterizedTypeReference.class))).thenReturn(Mono.just(List.of(new Evento())));

        String result = eventoHandler.eventos(mensaje, chatId);
        verify(telegramBot).enviarMensaje(eq(chatId), contains("Los eventos son"));

        assert result.isEmpty();
    }
}
