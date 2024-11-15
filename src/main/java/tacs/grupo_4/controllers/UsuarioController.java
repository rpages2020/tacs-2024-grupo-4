package tacs.grupo_4.controllers;

import org.springframework.context.annotation.Profile;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import tacs.grupo_4.entities.Evento;
import tacs.grupo_4.entities.Ticket;
import tacs.grupo_4.entities.Usuario;
import tacs.grupo_4.exceptions.UsuarioNotFoundException;
import tacs.grupo_4.exceptions.UsuarioYaExisteException;
import tacs.grupo_4.services.EventoService;
import tacs.grupo_4.services.TicketServicio;
import tacs.grupo_4.services.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


import java.util.List;
import java.util.UUID;
@Profile("api")
@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {
    @Autowired
    private TicketServicio ticketService;
    @Autowired
    private UsuarioService usuarioService;
    @Autowired
    private EventoService eventoService;

    @Operation(summary = "Obtener todos los usuarios", description = "Devuelve una lista de todos los usuarios disponibles en la plataforma.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuarios recuperados exitosamente",
                    content = { @Content(mediaType = "application/json", schema = @Schema(implementation = Usuario.class)) })
    })
    @GetMapping("")
    public ResponseEntity<List<Usuario>> obtenerTodosLosUsuarios() {
        List<Usuario> usuarios = usuarioService.obtenerTodosLosUsuarios();
        return new ResponseEntity<>(usuarios, HttpStatus.OK);
    }
    @Operation(summary = "Obtener usuario por Telegram ID", description = "Recupera un usuario de la base de datos utilizando su ID de Telegram.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuario encontrado",
                    content = { @Content(mediaType = "application/json", schema = @Schema(implementation = Usuario.class)) }),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado", content = @Content)
    })
    @GetMapping("/telegram/{id}")
    public ResponseEntity<Usuario> obtenerUsuarioDeTelegram(@PathVariable String id) {
        try {
            Usuario usuario = usuarioService.obtenerUsuarioPorTelegramId(Long.parseLong(id));
            return new ResponseEntity<>(usuario, HttpStatus.OK);
        } catch (UsuarioNotFoundException e) {
            return new ResponseEntity<>(new Usuario(), HttpStatus.NOT_FOUND);
        }
    }

    @Operation(summary = "Crear un nuevo usuario", description = "Permite registrar un nuevo usuario en la plataforma.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuario creado exitosamente",
                    content = { @Content(mediaType = "application/json", schema = @Schema(implementation = Usuario.class)) }),
            @ApiResponse(responseCode = "409", description = "El usuario ya existe", content = @Content)
    })
    @PostMapping
    public ResponseEntity<Usuario> crearUsuario(@RequestBody Usuario usuario) {
        try {
            usuarioService.crearUsuario(usuario);
            return new ResponseEntity<>(usuario, HttpStatus.OK);
        } catch (UsuarioYaExisteException e) {
            return new ResponseEntity<>(usuario, HttpStatus.CONFLICT);
        }
    }

    @Operation(summary = "Obtener tickets de un usuario", description = "Devuelve una lista de tickets reservados por un usuario específico.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Tickets recuperados exitosamente",
                    content = { @Content(mediaType = "application/json", schema = @Schema(implementation = Ticket.class)) })
    })
    @GetMapping("/{id}/tickets")
    public ResponseEntity<List<Ticket>> obtenerTicketsDeUsuario(@PathVariable String id) {
        List<Ticket> tickets = ticketService.obtenerTicketsPorUsuario(UUID.fromString(id));
        return new ResponseEntity<>(tickets, HttpStatus.OK);
    }

    @Operation(summary = "Cancelar una reserva de ticket", description = "Permite a un usuario cancelar una reserva de ticket.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Reserva cancelada exitosamente", content = @Content)
    })
    @DeleteMapping("/{id}/tickets/{ticketId}")
    public ResponseEntity<Void> cancelarReserva(@PathVariable String id, @PathVariable String ticketId) {
        ticketService.cancelarTicket(UUID.fromString(ticketId));
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Operation(summary = "Obtener eventos de un usuario", description = "Devuelve una lista de eventos relacionados con un usuario específico.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Eventos recuperados exitosamente",
                    content = { @Content(mediaType = "application/json", schema = @Schema(implementation = Evento.class)) })
    })
    @GetMapping("/{id}/eventos")
    public ResponseEntity<List<Evento>> obtenerEventosDeUsuario(@PathVariable String id) {
        List<Evento> eventos = eventoService.obtenerEventosPorUserId(UUID.fromString(id));
        return new ResponseEntity<>(eventos, HttpStatus.OK);
    }

    @GetMapping("/{id}/reservas")
    public ResponseEntity<List<Evento>> obtenerReservasDeUsuario(@PathVariable String id) {
        List<Evento> eventos = eventoService.obtenerEventosPorUserId(UUID.fromString(id));
        return new ResponseEntity<>(eventos, HttpStatus.OK);
    }

    @GetMapping("/fechaAlta/{fecha}")
    public long altasPorFecha(@PathVariable String fecha) {
        return usuarioService.cantidadUsuariosPorFecha(fecha);
    }

    @GetMapping("/entreFechas/{fecha1}/{fecha2}")
    public long altasEntreFechas(@PathVariable String fecha1, @PathVariable String fecha2) {
        return usuarioService.cantidadAltasEntreFechas(fecha1, fecha2);
    }

}
