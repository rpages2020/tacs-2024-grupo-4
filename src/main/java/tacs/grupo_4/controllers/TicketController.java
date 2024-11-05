package tacs.grupo_4.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tacs.grupo_4.entities.Ticket;
import tacs.grupo_4.services.TicketServicio;
import java.util.UUID;

@Profile("api")
@RestController
@RequestMapping("/api/reservas")
@Slf4j
public class TicketController {
    @Autowired
    private TicketServicio ticketService;

    @DeleteMapping("/{id}")
    public ResponseEntity<Ticket> cancelarTicket(@PathVariable String id) {
        return new ResponseEntity<>(ticketService.cancelarTicket(UUID.fromString(id)), HttpStatus.CREATED);
    }


}
