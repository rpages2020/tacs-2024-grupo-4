package tacs.grupo_4.services;

import org.springframework.context.annotation.Profile;
import tacs.grupo_4.entities.Asiento;
import tacs.grupo_4.entities.Ticket;
import tacs.grupo_4.repositories.AsientoRepository;
import tacs.grupo_4.repositories.TicketRepository;
import org.springframework.stereotype.Service;
import tacs.grupo_4.repositories.UsuarioRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Profile("api")
@Service
public class TicketServicio {

    private final TicketRepository ticketRepository;
    private final UsuarioRepository usuarioRepository;
    private final AsientoRepository asientoRepository;
    private final AsientoService asientoService;

    public TicketServicio(TicketRepository ticketRepository,
                          UsuarioRepository usuarioRepository,
                          AsientoRepository asientoRepository,
                          AsientoService asientoService) {
        this.ticketRepository = ticketRepository;
        this.usuarioRepository = usuarioRepository;
        this.asientoRepository = asientoRepository;
        this.asientoService = asientoService;
    }

    public Ticket crearTicket(Ticket ticket) {
        return ticketRepository.save(ticket);
    }

    public List<Ticket> obtenerTicketsPorUsuario(UUID usuarioId) {
        return ticketRepository.findByAsientoUsuario(usuarioId);
    }
    public Ticket obtenerTicketPorId(UUID id) {
        return ticketRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Ticket no encontrado"));
    }

    public Ticket cancelarTicket(UUID id) {
        Ticket ticketExistente = obtenerTicketPorId(id);
        ticketExistente.setEstaActivo(false);
        asientoService.liberarAsiento(ticketExistente.getAsiento().getId());
        return ticketRepository.save(ticketExistente);
    }

    public Ticket crearTicketDeAsiento(Asiento asiento) {
        Ticket ticket = new Ticket(
                UUID.randomUUID(),
                asiento.getSector().getPrecio(),
                asiento,
                LocalDateTime.now(),
                true
                );
        return ticketRepository.insert(ticket);
    }
}
