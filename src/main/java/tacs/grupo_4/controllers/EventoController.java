package tacs.grupo_4.controllers;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import tacs.grupo_4.entities.Asiento;
import tacs.grupo_4.entities.Evento;
import tacs.grupo_4.entities.Ticket;
import tacs.grupo_4.entities.Sector;
import tacs.grupo_4.exceptions.AsientoNotFoundException;
import tacs.grupo_4.repositories.EventoRepository;
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

@Profile("api")
@RestController
@RequestMapping("/api/eventos")
public class EventoController {

    @Autowired
    private EventoService eventoService;
    @Autowired
    private TicketServicio ticketService;
    @Autowired
    private EventoRepository eventoRepository;

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


    @PostMapping("/{eventoId}/sector/{sectorNombre}/{asientoNro}/{usuarioId}")
    public ResponseEntity<Ticket> reservarAsiento(
            @PathVariable String eventoId,
            @PathVariable String sectorNombre,
            @PathVariable String asientoNro,
            @PathVariable String usuarioId) {

        // TOD0: en realidad ese usuario ID saldría de un JWT/Cookie
        // Esto es seguro si solo el bot tiene acceso a la api
        Asiento asiento = eventoService.reservarAsiento(UUID.fromString(eventoId), sectorNombre, asientoNro, UUID.fromString(usuarioId));
        return new ResponseEntity<>(ticketService.crearTicketDeAsiento(asiento), HttpStatus.OK);
    }

    @PutMapping("/{eventoId}/sector/{sectorNombre}/usuario/{usuarioId}")
    public ResponseEntity<Ticket> reservarAsientoRandom(
            @PathVariable String eventoId,
            @PathVariable String sectorNombre,
            @PathVariable String usuarioId) {

        // TOD0: en realidad ese usuario ID saldría de un JWT/Cookie
        // Esto es seguro si solo el bot tiene acceso a la api
        if (eventoActivo(eventoId)) {
            try {
                Asiento asiento = eventoService.reservarAsientoRandom(UUID.fromString(eventoId), sectorNombre, UUID.fromString(usuarioId));
                return new ResponseEntity<>(ticketService.crearTicketDeAsiento(asiento), HttpStatus.OK);
            } catch (AsientoNotFoundException e) {
                return new ResponseEntity<>(null, HttpStatus.PRECONDITION_FAILED);
            } catch (OptimisticLockingFailureException e) {
                return new ResponseEntity<>(null, HttpStatus.CONFLICT);
            }
        } else {
            return new ResponseEntity<>(null, HttpStatus.CONFLICT);
        }
    }

    private boolean eventoActivo(String eventoId) {
        return eventoRepository.findById(UUID.fromString(eventoId)).get().getEstaActivo();
    }

    @PostMapping("/{eventoId}/sector/")
    public ResponseEntity<Evento> crearSector(
            @PathVariable String eventoId,
            @RequestBody Sector sector) {
        return new ResponseEntity<>(eventoService.crearSector(UUID.fromString(eventoId), sector), HttpStatus.OK);
    }

    @PutMapping("/{eventoId}/confirmar/{usuarioId}")
    public ResponseEntity<Evento> confirmarEvento(
            @PathVariable String eventoId,
            @PathVariable String usuarioId) {

        return new ResponseEntity<>(eventoService.confirmarEvento(UUID.fromString(eventoId), UUID.fromString(usuarioId)), HttpStatus.OK);
    }

    @PutMapping("/{id}/usuario/{usuarioId}")
    public ResponseEntity<Void> cancelarEvento(@PathVariable String id, @PathVariable String usuarioId) {
        System.out.println("LLEGA AL CONTROLLER EVENTO_ID: " + id + " Usuario: " + usuarioId);
        eventoService.cancelarEvento(UUID.fromString(id), UUID.fromString(usuarioId));
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @DeleteMapping("/{id}/usuario/{usuarioId}")
    public ResponseEntity<Void> eliminarEvento(@PathVariable String id, @PathVariable String usuarioId) {
        System.out.println("LLEGA AL CONTROLLER EVENTO_ID: " + id + " Usuario: " + usuarioId);
        eventoService.eliminarEvento(UUID.fromString(id), UUID.fromString(usuarioId));
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

}
