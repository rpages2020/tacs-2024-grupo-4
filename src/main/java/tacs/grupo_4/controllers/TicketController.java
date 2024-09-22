package tacs.grupo_4.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tacs.grupo_4.entities.Ticket;
import tacs.grupo_4.services.TicketServicio;
import java.util.UUID;


@RestController
@RequestMapping("/api/tickets")
@Slf4j
public class TicketController {
    @Autowired
    private TicketServicio ticketService;
    @DeleteMapping("/{id}")
    public ResponseEntity<Ticket> cancelarTicket(@PathVariable String id) {
        return new ResponseEntity<>(ticketService.cancelarTicket(UUID.fromString(id)), HttpStatus.CREATED);
    }
}
