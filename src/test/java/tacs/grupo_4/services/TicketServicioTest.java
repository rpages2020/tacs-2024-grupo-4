package tacs.grupo_4.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import tacs.grupo_4.entities.Asiento;
import tacs.grupo_4.entities.Sector;
import tacs.grupo_4.entities.Ticket;
import tacs.grupo_4.repositories.AsientoRepository;
import tacs.grupo_4.repositories.TicketRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

class TicketServicioTest {

    @Mock
    private TicketRepository ticketRepository;

    @Mock
    private AsientoRepository asientoRepository;

    @Mock
    private AsientoService asientoService;

    @InjectMocks
    private TicketServicio ticketServicio;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCrearTicket() {
        Ticket ticket = new Ticket();
        when(ticketRepository.save(ticket)).thenReturn(ticket);

        Ticket createdTicket = ticketServicio.crearTicket(ticket);

        assertNotNull(createdTicket);
        verify(ticketRepository).save(ticket);
    }

    @Test
    void testCancelarTicketSuccess() {
        UUID ticketId = UUID.randomUUID();
        Ticket ticket = new Ticket();
        Asiento asiento = new Asiento();
        asiento.setId(UUID.randomUUID());
        ticket.setAsiento(asiento);

        when(ticketRepository.findById(ticketId)).thenReturn(Optional.of(ticket));

        ticketServicio.cancelarTicket(ticketId);

        assertFalse(ticket.isEstaActivo());
        verify(asientoService).liberarAsiento(asiento.getId());
        verify(ticketRepository).save(ticket);
    }

    @Test
    void testCancelarTicketNotFound() {
        UUID ticketId = UUID.randomUUID();
        when(ticketRepository.findById(ticketId)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> ticketServicio.cancelarTicket(ticketId));
    }
    @Test
    void testObtenerTicketsPorUsuario() {
        UUID usuarioId = UUID.randomUUID();
        List<Ticket> tickets = List.of(new Ticket(), new Ticket());

        when(ticketRepository.findByAsientoUsuario(usuarioId)).thenReturn(tickets);

        List<Ticket> resultado = ticketServicio.obtenerTicketsPorUsuario(usuarioId);

        assertEquals(tickets.size(), resultado.size());
        verify(ticketRepository).findByAsientoUsuario(usuarioId);
    }
    @Test
    void testObtenerTicketPorIdSuccess() {
        UUID ticketId = UUID.randomUUID();
        Ticket ticket = new Ticket();

        when(ticketRepository.findById(ticketId)).thenReturn(Optional.of(ticket));

        Ticket resultado = ticketServicio.obtenerTicketPorId(ticketId);

        assertNotNull(resultado);
        verify(ticketRepository).findById(ticketId);
    }
    @Test
    void testCrearTicketDeAsiento() {
        Asiento asiento = new Asiento();
        asiento.setSector(new Sector());
        asiento.getSector().setPrecio(100.0);

        Ticket ticket = new Ticket(UUID.randomUUID(), 100.0, asiento, LocalDateTime.now(), true, "Ejemplo", "sector ejemplo");

        when(ticketRepository.insert(any(Ticket.class))).thenReturn(ticket);

        Ticket resultado = ticketServicio.crearTicketDeAsiento(asiento);

        assertNotNull(resultado);
        assertEquals(asiento, resultado.getAsiento());
        verify(ticketRepository).insert(any(Ticket.class));
    }


}
