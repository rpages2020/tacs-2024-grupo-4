package tacs.grupo_4.services;

import tacs.grupo_4.entities.Ticket;
import tacs.grupo_4.repositories.TicketRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TicketServicio {

    @Autowired
    private TicketRepository ticketRepository;

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
        ticketExistente.setReserva(ticketActualizado.getReserva());
       //ticketExistente.setUbicacion(ticketActualizado.getUbicacion());// un ticket pertence a una ubicacion todo el tiempo
        ticketExistente.setPrecio(ticketActualizado.getPrecio()); //manejar en el front?
        ticketExistente.setUsuario(ticketActualizado.getUsuario());
        ticketExistente.setReservado(ticketActualizado.getReservado());
        return ticketRepository.save(ticketExistente);
    }
}
