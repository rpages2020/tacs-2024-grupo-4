package tacs.grupo_4.controllers;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import tacs.grupo_4.entities.Asiento;
import tacs.grupo_4.entities.Usuario;
import tacs.grupo_4.entities.Evento;
import tacs.grupo_4.entities.Ticket;
import tacs.grupo_4.entities.Sector;
import tacs.grupo_4.services.EventoService;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import tacs.grupo_4.services.TicketServicio;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/eventos")
public class EventoController {

    @Autowired
    private EventoService eventoService;
    @Autowired
    private TicketServicio ticketService;

    @PostMapping
    public ResponseEntity<Evento> crearEvento(@RequestBody Evento evento) {
        Evento eventoCreado = eventoService.crearEvento(evento);
        return new ResponseEntity<>(eventoCreado, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<Evento>> obtenerTodosLosEventos() {
        List<Evento> eventos = eventoService.obtenerTodosLosEventos();
        return new ResponseEntity<>(eventos, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Evento> obtenerEventoPorId(@PathVariable String id) {
        Evento evento = eventoService.obtenerEventoPorId(UUID.fromString(id));
        return new ResponseEntity<>(evento, HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Evento> actualizarEvento(@PathVariable String id, @RequestBody Evento evento) {
        Evento eventoActualizado = eventoService.actualizarEvento(UUID.fromString(id), evento);
        return new ResponseEntity<>(eventoActualizado, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> cancelarEvento(@PathVariable String id) {
        eventoService.cancelarEvento(UUID.fromString(id));
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PutMapping("/{eventoId}/sector/{sectorId}/{asientoNro}")
    public ResponseEntity<Ticket> reservarAsiento(
            @PathVariable String eventoId,
            @PathVariable String sectorId,
            @PathVariable String asientoNro) {

        Usuario usuario = new Usuario(UUID.randomUUID(), "juan", "a@hola.com", 1);
        // TOD0: Arriba iría algo como un getCurrentUserId cuando haya autenticacion.
        Asiento asiento = eventoService.reservarAsiento(UUID.fromString(eventoId), UUID.fromString(sectorId), asientoNro, usuario.getId());
        return new ResponseEntity<>(ticketService.crearTicketDeAsiento(asiento), HttpStatus.OK);
    }
    @PostMapping("/{eventoId}/sector/")
    public ResponseEntity<Evento> crearSector(
            @PathVariable String eventoId,
            @RequestBody Sector sector) {
        return new ResponseEntity<>(eventoService.crearSector(UUID.fromString(eventoId), sector), HttpStatus.OK);
    }
}
