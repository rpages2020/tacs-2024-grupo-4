package tacs.grupo_4.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;
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


    @Operation(summary = "Cancelar un ticket", description = "Permite a un usuario cancelar un ticket previamente reservado para un evento.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Ticket cancelado exitosamente",
                    content = { @Content(mediaType = "application/json", schema = @Schema(implementation = Ticket.class)) }),
            @ApiResponse(responseCode = "404", description = "Ticket no encontrado", content = @Content)
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Ticket> cancelarTicket(@PathVariable String id) {
        return new ResponseEntity<>(ticketService.cancelarTicket(UUID.fromString(id)), HttpStatus.CREATED);
    }

    @GetMapping("/fecha/{fecha}")
    public long cantidadTicketsPorFecha(@PathVariable String fecha) {
        return ticketService.cantidadTicketsPorFecha(fecha);
    }

    @GetMapping("/entreFechas/{fecha1}/{fecha2}")
    public long listarTicketsPorFecha(@PathVariable String fecha1, @PathVariable String fecha2) {
        return ticketService.cantidadTicketsEntreFechas(fecha1, fecha2);
    }
}
