package tacs.grupo_4.services;

import org.springframework.context.annotation.Profile;
import org.springframework.dao.DuplicateKeyException;
import tacs.grupo_4.entities.Ticket;
import tacs.grupo_4.entities.Usuario;
import tacs.grupo_4.exceptions.UsuarioNotFoundException;
import tacs.grupo_4.exceptions.UsuarioYaExisteException;
import tacs.grupo_4.repositories.UsuarioRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Profile("api")
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
    public List<Usuario> obtenerTodosLosUsuarios() {
        return usuarioRepository.findAll();
    }

    public long cantidadUsuariosPorFecha(String fecha) {
        return usuarioRepository.countByFechaAlta(fecha);
    }

   public long cantidadAltasEntreFechas(String fecha1, String fecha2) {
        return  usuarioRepository.countByFechaAltaBetween(fecha1, fecha2);
   }

    public void modoAdminOn(UUID id) {
       Optional<Usuario> usuario = usuarioRepository.findById(id);
       usuario.get().setModoAdmin(true);
    }

    public void modoAdminOff(UUID id) {
        Optional<Usuario> usuario = usuarioRepository.findById(id);
        usuario.get().setModoAdmin(false);
    }
}
