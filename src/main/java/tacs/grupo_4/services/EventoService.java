package tacs.grupo_4.services;

import tacs.grupo_4.entities.Evento;
import tacs.grupo_4.repositories.EventoRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EventoService {

    private final EventoRepository eventoRepository;

    public EventoService(EventoRepository eventoRepository) {
        this.eventoRepository = eventoRepository;
    }

    public Evento crearEvento(Evento evento) {
        return eventoRepository.save(evento);
    }

    public List<Evento> obtenerTodosLosEventos() {
        return eventoRepository.findAll();
    }

    public Evento obtenerEventoPorId(String id) {
        return eventoRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Evento no encontrado"));
    }

    public void eliminarEvento(String id) {
        eventoRepository.deleteById(id);
    }

    public Evento actualizarEvento(String id, Evento eventoActualizado) {
        Evento eventoExistente = obtenerEventoPorId(id);
        eventoExistente.setNombre(eventoActualizado.getNombre());
        eventoExistente.setFecha(eventoActualizado.getFecha());
        return eventoRepository.save(eventoExistente);
    }
}
