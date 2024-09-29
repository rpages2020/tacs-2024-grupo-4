package tacs.grupo_4.services;

import org.springframework.dao.DuplicateKeyException;
import tacs.grupo_4.entities.Ticket;
import tacs.grupo_4.entities.Usuario;
import tacs.grupo_4.exceptions.UsuarioNotFoundException;
import tacs.grupo_4.exceptions.UsuarioYaExisteException;
import tacs.grupo_4.repositories.UsuarioRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final TicketServicio ticketService;

    public UsuarioService(UsuarioRepository usuarioRepository, TicketServicio ticketService) {
        this.usuarioRepository = usuarioRepository;
        this.ticketService = ticketService;
    }

    public Usuario crearUsuario(Usuario usuario) {
        try {
            return usuarioRepository.save(usuario);
        } catch (DuplicateKeyException e) {
            throw new UsuarioYaExisteException(usuario);
        }
    }

    public List<Ticket> obtenerTicketsDeUsuario(String id) {
        return ticketService.obtenerTicketsPorUsuario(UUID.fromString(id));
    }

    public Usuario obtenerUsuarioPorId(String id) {
        return usuarioRepository.findById(UUID.fromString(id))
            .orElseThrow(UsuarioNotFoundException::new);
    }

    public Usuario obtenerUsuarioPorTelegramId(Long id) {
        return usuarioRepository.findByTelegramUserId(id)
                .orElseThrow(UsuarioNotFoundException::new);
    }
}
