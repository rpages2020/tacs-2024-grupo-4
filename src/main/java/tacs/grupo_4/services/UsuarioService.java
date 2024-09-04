package tacs.grupo_4.services;

import tacs.grupo_4.entities.Ticket;
import tacs.grupo_4.entities.Usuario;
import tacs.grupo_4.repositories.UsuarioRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;

    public UsuarioService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    public Usuario crearUsuario(Usuario usuario) {
        return usuarioRepository.save(usuario);
    }

    public List<Ticket> obtenerTicketsDeUsuario(String id) {
        Usuario usuario = obtenerUsuarioPorId(id);
        return usuario.getTickets();
    }

    public Ticket reservarTicket(String id, Ticket ticket) {
        Usuario usuario = obtenerUsuarioPorId(id);
        usuario.getTickets().add(ticket);
        usuarioRepository.save(usuario);
        return ticket;
    }

    public void cancelarReserva(String id, String ticketId) {
        Usuario usuario = obtenerUsuarioPorId(id);
        usuario.getTickets().removeIf(ticket -> ticket.getId().equals(Integer.valueOf(ticketId)));
        usuarioRepository.save(usuario);
    }

    private Usuario obtenerUsuarioPorId(String id) {
        return usuarioRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
    }
}
