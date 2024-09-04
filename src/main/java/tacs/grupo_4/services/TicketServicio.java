package tacs.grupo_4.services;

import tacs.grupo_4.entities.Ticket;
import tacs.grupo_4.repositories.TicketRepository;
import org.springframework.stereotype.Service;

@Service
public class TicketServicio {

    private final TicketRepository ticketRepository;

    public TicketServicio(TicketRepository ticketRepository) {
        this.ticketRepository = ticketRepository;
    }

    public Ticket crearTicket(Ticket ticket) {
        return ticketRepository.save(ticket);
    }

    public Ticket obtenerTicketPorId(Long id) {
        return ticketRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Ticket no encontrado"));
    }

    public void eliminarTicket(Long id) {
        ticketRepository.deleteById(id);
    }

    public Ticket actualizarTicket(Long id, Ticket ticketActualizado) {
        Ticket ticketExistente = obtenerTicketPorId(id);
        ticketExistente.setUbicacion(ticketActualizado.getUbicacion()); // un ticket pertence a una ubicacion
        ticketExistente.setPrecio(ticketActualizado.getPrecio()); //manejar en el front?
        ticketExistente.setUsuario(ticketActualizado.getUsuario());
        return ticketRepository.save(ticketExistente);
    }
}
