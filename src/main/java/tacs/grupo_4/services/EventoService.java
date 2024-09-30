package tacs.grupo_4.services;

import org.springframework.transaction.annotation.Transactional;
import tacs.grupo_4.entities.Asiento;
import tacs.grupo_4.entities.Evento;
import tacs.grupo_4.entities.Sector;
import tacs.grupo_4.exceptions.AsientoNotFoundException;
import tacs.grupo_4.repositories.AsientoRepository;
import tacs.grupo_4.repositories.EventoRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class EventoService {


    private final EventoRepository eventoRepository;
    private final AsientoRepository asientoRepository;
    public EventoService(EventoRepository eventoRepository, AsientoRepository asientoRepository) {
        this.eventoRepository = eventoRepository;
        this.asientoRepository = asientoRepository;
    }

    public Evento crearEvento(Evento evento) {
        return eventoRepository.save(evento);
    }

    public List<Evento> obtenerTodosLosEventos() {
        return eventoRepository.findAll();
    }

    public Evento obtenerEventoPorId(UUID id) {
        return eventoRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Evento no encontrado"));
    }
    public List<Evento> obtenerEventosPorUserId(UUID id) {
        return eventoRepository.findByUsuario(id)
                .orElseThrow(() -> new RuntimeException("Usuario sin eventos"));
    }
    public Evento cancelarEvento(UUID id) {
        Evento eventoExistente = obtenerEventoPorId(id);
        eventoExistente.setEstaActivo(!eventoExistente.getEstaActivo());
        return eventoRepository.save(eventoExistente);
    }
    public Evento actualizarEvento(UUID id, Evento eventoActualizado) {
        Evento eventoExistente = obtenerEventoPorId(id);
        eventoExistente.setNombre(eventoActualizado.getNombre());
        eventoExistente.setFecha(eventoActualizado.getFecha());
        return eventoRepository.save(eventoExistente);
    }

    @Transactional
    public Asiento reservarAsiento(UUID eventoId, UUID sectorId, String nroAsiento, UUID usuario) {
        List<Asiento> asientos = asientoRepository.findByEventoIdAndSectorIdAndNroAsientoAndEstaReservado(eventoId, sectorId, nroAsiento, false);
        if (asientos.isEmpty()) {
            throw new AsientoNotFoundException();
        }

        Asiento asiento = asientos.get(0);
        asiento.setEstaReservado(true);
        asiento.setUsuario(usuario);
        asiento.setReservadoEn(LocalDateTime.now());
        return asientoRepository.save(asiento);
    }

    public Evento crearSector(UUID eventoId, Sector sector) {
        Evento evento = eventoRepository.findById(eventoId)
                .orElseThrow(() -> new RuntimeException("Evento no encontrado"));
        List<Sector> sectores = evento.getSectores();
        sectores.add(sector);
        evento.setSectores(sectores);
        return eventoRepository.save(evento);
    }
}
