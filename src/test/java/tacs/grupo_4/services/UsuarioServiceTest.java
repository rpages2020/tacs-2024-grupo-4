package tacs.grupo_4.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.dao.DuplicateKeyException;
import tacs.grupo_4.entities.Ticket;
import tacs.grupo_4.entities.Usuario;
import tacs.grupo_4.exceptions.UsuarioNotFoundException;
import tacs.grupo_4.exceptions.UsuarioYaExisteException;
import tacs.grupo_4.repositories.UsuarioRepository;
import tacs.grupo_4.services.TicketServicio;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

class UsuarioServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private TicketServicio ticketServicio;

    @InjectMocks
    private UsuarioService usuarioService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCrearUsuarioSuccess() {
        Usuario usuario = new Usuario();
        when(usuarioRepository.save(usuario)).thenReturn(usuario);

        Usuario createdUsuario = usuarioService.crearUsuario(usuario);

        assertNotNull(createdUsuario);
        verify(usuarioRepository).save(usuario);
    }

    @Test
    void testObtenerUsuarioPorIdSuccess() {
        UUID usuarioId = UUID.randomUUID();
        Usuario usuario = new Usuario();
        usuario.setId(usuarioId);

        when(usuarioRepository.findById(usuarioId)).thenReturn(Optional.of(usuario));

        Usuario foundUsuario = usuarioService.obtenerUsuarioPorId(usuarioId.toString());

        assertNotNull(foundUsuario);
        assertEquals(usuarioId, foundUsuario.getId());
    }

    @Test
    void testObtenerUsuarioPorIdNotFound() {
        UUID usuarioId = UUID.randomUUID();
        when(usuarioRepository.findById(usuarioId)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> usuarioService.obtenerUsuarioPorId(usuarioId.toString()));
    }
    @Test
    void testCrearUsuarioUsuarioYaExiste() {
        Usuario usuario = new Usuario();
        when(usuarioRepository.save(usuario)).thenThrow(new DuplicateKeyException("Duplicate key"));

        assertThrows(UsuarioYaExisteException.class, () -> usuarioService.crearUsuario(usuario));
        verify(usuarioRepository).save(usuario);
    }
    @Test
    void testObtenerTicketsDeUsuario() {
        UUID usuarioId = UUID.randomUUID();
        List<Ticket> tickets = List.of(new Ticket(), new Ticket());

        when(ticketServicio.obtenerTicketsPorUsuario(usuarioId)).thenReturn(tickets);

        List<Ticket> resultado = usuarioService.obtenerTicketsDeUsuario(usuarioId.toString());

        assertEquals(tickets.size(), resultado.size());
        verify(ticketServicio).obtenerTicketsPorUsuario(usuarioId);
    }
    @Test
    void testObtenerUsuarioPorTelegramIdSuccess() {
        Long telegramId = 123456789L;
        Usuario usuario = new Usuario();
        usuario.setTelegramUserId(telegramId);

        when(usuarioRepository.findByTelegramUserId(telegramId)).thenReturn(Optional.of(usuario));

        Usuario resultado = usuarioService.obtenerUsuarioPorTelegramId(telegramId);

        assertNotNull(resultado);
        assertEquals(telegramId, resultado.getTelegramUserId());
        verify(usuarioRepository).findByTelegramUserId(telegramId);
    }

    @Test
    void testObtenerUsuarioPorTelegramIdNotFound1() {
        Long telegramId = 123456789L;
        when(usuarioRepository.findByTelegramUserId(telegramId)).thenReturn(Optional.empty());

        assertThrows(UsuarioNotFoundException.class, () -> usuarioService.obtenerUsuarioPorTelegramId(telegramId));
        verify(usuarioRepository).findByTelegramUserId(telegramId);
    }
    @Test
    void testObtenerUsuarioPorTelegramIdSuccess1() {
        Long telegramId = 123456789L;
        Usuario usuario = new Usuario();
        usuario.setTelegramUserId(telegramId);

        when(usuarioRepository.findByTelegramUserId(telegramId)).thenReturn(Optional.of(usuario));

        Usuario resultado = usuarioService.obtenerUsuarioPorTelegramId(telegramId);

        assertNotNull(resultado);
        assertEquals(telegramId, resultado.getTelegramUserId());
        verify(usuarioRepository).findByTelegramUserId(telegramId);
    }

    @Test
    void testObtenerUsuarioPorTelegramIdNotFound() {
        Long telegramId = 123456789L;
        when(usuarioRepository.findByTelegramUserId(telegramId)).thenReturn(Optional.empty());

        assertThrows(UsuarioNotFoundException.class, () -> usuarioService.obtenerUsuarioPorTelegramId(telegramId));
        verify(usuarioRepository).findByTelegramUserId(telegramId);
    }
    @Test
    void testObtenerTodosLosUsuarios() {
        List<Usuario> usuarios = List.of(new Usuario(), new Usuario());

        when(usuarioRepository.findAll()).thenReturn(usuarios);

        List<Usuario> resultado = usuarioService.obtenerTodosLosUsuarios();

        assertEquals(usuarios.size(), resultado.size());
        verify(usuarioRepository).findAll();
    }

    @Test
    void cantidadusuariosalta() {
        System.out.println(usuarioRepository.countByFechaAltaBetween("11-11-24","16-11-24"));
    }


}
