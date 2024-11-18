package tacs.grupo_4.services;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import tacs.grupo_4.entities.Estadisticas;
import tacs.grupo_4.repositories.EventoRepository;
import tacs.grupo_4.repositories.TicketRepository;
import tacs.grupo_4.repositories.UsuarioRepository;

@Profile("api")
@Service
public class EstadisticasService {
    private final EventoRepository eventoRepository;
    private final UsuarioRepository usuarioRepository;
    private final TicketRepository ticketRepository;

    public EstadisticasService(EventoRepository eventoRepository, UsuarioRepository usuarioRepository, TicketRepository ticketRepository) {
        this.eventoRepository = eventoRepository;
        this.usuarioRepository = usuarioRepository;
        this.ticketRepository = ticketRepository;
    }

    public Estadisticas estadisticas() {
        Estadisticas resumen = new Estadisticas();
        resumen.setTotalEventos(eventoRepository.count());
        resumen.setTotalUsuarios(usuarioRepository.count());
        resumen.setTotalVentas(ticketRepository.count());
        resumen.setTotalRecaudado(ticketRepository.sumAllPrecios().doubleValue());
        return resumen;
    }

}
