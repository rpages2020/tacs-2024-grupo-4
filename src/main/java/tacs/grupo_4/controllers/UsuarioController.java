package tacs.grupo_4.controllers;

import tacs.grupo_4.entities.Ticket;
import tacs.grupo_4.entities.Usuario;
import tacs.grupo_4.services.TicketServicio;
import tacs.grupo_4.services.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;


import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {
    @Autowired
    private TicketServicio ticketService;
    @Autowired
    private UsuarioService usuarioService;

    @GetMapping("/telegram/{id}")
    public ResponseEntity<Usuario> obtenerUsuarioDeTelegram(@PathVariable String id) {
        Usuario usuario = usuarioService.obtenerUsuarioPorTelegramId(Long.getLong(id));
        return new ResponseEntity<>(usuario, HttpStatus.OK);
    }
    @PostMapping
    public ResponseEntity<Usuario> crearUsuario(@RequestBody Usuario usuario) {
        Usuario usuarioCreado = usuarioService.crearUsuario(usuario);
        return new ResponseEntity<>(usuarioCreado, HttpStatus.CREATED);
    }

    @GetMapping("/{id}/tickets")
    public ResponseEntity<List<Ticket>> obtenerTicketsDeUsuario(@PathVariable String id) {
        List<Ticket> tickets = ticketService.obtenerTicketsPorUsuario(UUID.fromString(id));
        return new ResponseEntity<>(tickets, HttpStatus.OK);
    }

    @DeleteMapping("/{id}/tickets/{ticketId}")
    public ResponseEntity<Void> cancelarReserva(@PathVariable String id, @PathVariable String ticketId) {
        ticketService.cancelarTicket(UUID.fromString(ticketId));
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
