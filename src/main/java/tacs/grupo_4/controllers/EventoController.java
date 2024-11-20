package tacs.grupo_4.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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

    @Operation(summary = "Crear un nuevo evento", description = "Permite a un administrador publicar un nuevo evento. Un evento tiene nombre, fecha, ubicaciones y cantidad de tickets por ubicación.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Evento creado exitosamente",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Evento.class)) }),
            @ApiResponse(responseCode = "400", description = "Solicitud inválida", content = @Content)
    })
    @PostMapping
    public ResponseEntity<Evento> crearEvento(@RequestBody Evento evento) {
        Evento eventoCreado = eventoService.crearEvento(evento);
        return new ResponseEntity<>(eventoCreado, HttpStatus.CREATED);
    }

    @Operation(summary = "Obtener todos los eventos", description = "Devuelve una lista de todos los eventos publicados.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de eventos obtenida exitosamente",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Evento.class)) })
    })
    @GetMapping
    public ResponseEntity<List<Evento>> obtenerTodosLosEventos() {
        List<Evento> eventos = eventoService.obtenerTodosLosEventos();
        return new ResponseEntity<>(eventos, HttpStatus.OK);
    }

    @Operation(summary = "Obtener un evento por ID", description = "Permite a los usuarios o administradores obtener detalles de un evento específico mediante su ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Evento encontrado exitosamente",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Evento.class)) }),
            @ApiResponse(responseCode = "404", description = "Evento no encontrado", content = @Content)
    })
    @GetMapping("/{id}")
    public ResponseEntity<Evento> obtenerEventoPorId(@PathVariable String id) {
        Evento evento = eventoService.obtenerEventoPorId(UUID.fromString(id));
        return new ResponseEntity<>(evento, HttpStatus.OK);
    }

    @Operation(summary = "Actualizar un evento por ID", description = "Permite a un administrador actualizar los detalles de un evento específico.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Evento actualizado exitosamente",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Evento.class)) }),
            @ApiResponse(responseCode = "404", description = "Evento no encontrado", content = @Content)
    })
    @PutMapping("/{id}")
    public ResponseEntity<Evento> actualizarEvento(@PathVariable String id, @RequestBody Evento evento) {
        Evento eventoActualizado = eventoService.actualizarEvento(UUID.fromString(id), evento);
        return new ResponseEntity<>(eventoActualizado, HttpStatus.OK);
    }

    @Operation(summary = "Reservar un asiento", description = "Permite a un usuario reservar un asiento específico en un sector de un evento.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Asiento reservado exitosamente",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Ticket.class)) }),
            @ApiResponse(responseCode = "404", description = "Asiento no encontrado", content = @Content)
    })
    @PostMapping("/{eventoId}/sector/{sectorNombre}/{asientoNro}/{usuarioId}")
    public ResponseEntity<Ticket> reservarAsiento(
            @PathVariable String eventoId,
            @PathVariable String sectorNombre,
            @PathVariable String asientoNro,
            @PathVariable String usuarioId) {

        Asiento asiento = eventoService.reservarAsiento(UUID.fromString(eventoId), sectorNombre, asientoNro, UUID.fromString(usuarioId));
        return new ResponseEntity<>(ticketService.crearTicketDeAsiento(asiento), HttpStatus.OK);
    }

    @Operation(summary = "Reservar un asiento aleatorio", description = "Permite a un usuario reservar un asiento aleatorio en un sector de un evento.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Asiento reservado exitosamente",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Ticket.class)) }),
            @ApiResponse(responseCode = "412", description = "No se pudo encontrar un asiento disponible", content = @Content)
    })
    @PutMapping("/{eventoId}/sector/{sectorNombre}/usuario/{usuarioId}")
    public ResponseEntity<Ticket> reservarAsientoRandom(
            @PathVariable String eventoId,
            @PathVariable String sectorNombre,
            @PathVariable String usuarioId) {

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


    @Operation(summary = "Crear un sector", description = "Permite a un administrador crear un nuevo sector dentro de un evento.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Sector creado exitosamente",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Evento.class)) }),
            @ApiResponse(responseCode = "404", description = "Evento no encontrado", content = @Content)
    })
    @PostMapping("/{eventoId}/sector/")
    public ResponseEntity<Evento> crearSector(
            @PathVariable String eventoId,
            @RequestBody Sector sector) {
        return new ResponseEntity<>(eventoService.crearSector(UUID.fromString(eventoId), sector), HttpStatus.OK);
    }

    @Operation(summary = "Confirmar un evento", description = "Permite a un administrador confirmar un evento, cerrando la venta de tickets.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Evento confirmado exitosamente",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Evento.class)) }),
            @ApiResponse(responseCode = "404", description = "Evento no encontrado", content = @Content)
    })
    @PutMapping("/{eventoId}/confirmar/{usuarioId}")
    public ResponseEntity<Evento> confirmarEvento(
            @PathVariable String eventoId,
            @PathVariable String usuarioId) {

        return new ResponseEntity<>(eventoService.confirmarEvento(UUID.fromString(eventoId), UUID.fromString(usuarioId)), HttpStatus.OK);
    }

    @PutMapping("/{id}/usuario/{usuarioId}")
    public ResponseEntity<String> cancelarEvento(@PathVariable String id, @PathVariable long usuarioId) {
        eventoService.cancelarEvento(UUID.fromString(id), usuarioId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @DeleteMapping("/{id}/usuario/{usuarioId}")
    public ResponseEntity<String> eliminarEvento(@PathVariable String id, @PathVariable String usuarioId) {
        eventoService.eliminarEvento(UUID.fromString(id), UUID.fromString(usuarioId));
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PutMapping("/cambiarIdUsuario/{id}")
    public ResponseEntity<String> cambiarIdUsuario(@PathVariable String id) {
        eventoService.cambiarIdUsuario(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/fechaAlta/{fecha}")
    public long altasPorFecha(@PathVariable String fecha) {
        return eventoService.cantidadEventosPorFecha(fecha);
    }

    @GetMapping("/entreFechas/{fecha1}/{fecha2}")
    public long altasEntreFechas(@PathVariable String fecha1, @PathVariable String fecha2) {
        return eventoService.cantidadAltasEntreFechas(fecha1, fecha2);
    }

}
