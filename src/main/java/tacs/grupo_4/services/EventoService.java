package tacs.grupo_4.services;

import org.springframework.context.annotation.Profile;
import org.springframework.transaction.annotation.Transactional;
import tacs.grupo_4.entities.Asiento;
import tacs.grupo_4.entities.Evento;
import tacs.grupo_4.entities.Sector;
import tacs.grupo_4.exceptions.AsientoNotFoundException;
import tacs.grupo_4.exceptions.EventoNotFoundException;
import tacs.grupo_4.repositories.AsientoRepository;
import tacs.grupo_4.repositories.EventoRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Profile("api")
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

    public Evento actualizarEvento(UUID id, Evento eventoActualizado) {
        Evento eventoExistente = obtenerEventoPorId(id);
        eventoExistente.setNombre(eventoActualizado.getNombre());
        eventoExistente.setFecha(eventoActualizado.getFecha());
        return eventoRepository.save(eventoExistente);
    }

    @Transactional
    public Asiento reservarAsiento(UUID eventoId, String sectorNombre, String nroAsiento, UUID usuario) {
        List<Asiento> asientos = asientoRepository.findByEventoIdAndSectorNombreAndNroAsientoAndEstaReservado(eventoId, sectorNombre, nroAsiento, false);
        if (asientos.isEmpty()) {
            throw new AsientoNotFoundException();
        }

        Asiento asiento = asientos.getFirst();
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

    @Transactional
    public Asiento reservarAsientoRandom(UUID evento, String sectorNombre, UUID usuario) {
        Asiento asiento = asientoRepository.findFirstByEstaReservadoAndEventoIdAndSectorNombre(false, evento, sectorNombre)
                .orElseThrow(AsientoNotFoundException::new);
        asiento.setEstaReservado(true);
        asiento.setUsuario(usuario);
        asiento.setReservadoEn(LocalDateTime.now());
        this.sumarReserva(evento, sectorNombre);
        return asientoRepository.save(asiento);
    }

    public void sumarReserva(UUID eventoId, String nombreSector) {
        Evento evento = eventoRepository.findById(eventoId).orElseThrow(() -> new RuntimeException("Evento no encontrado"));
        evento.sumarVentaEnSector(nombreSector);
        eventoRepository.save(evento);
    }

    public Evento confirmarEvento(UUID eventoId, UUID usuarioId) {
        Evento evento = eventoRepository.findById(eventoId)
                .orElseThrow(EventoNotFoundException::new);
        if (!usuarioId.equals(evento.getUsuario()) || evento.getEstaConfirmado()) {
            throw new RuntimeException("Acceso denegado.");
        }
        evento.setEstaConfirmado(true);
        eventoRepository.save(evento);
        List<Asiento> asientos = new ArrayList<>();
        List<Sector> sectores = evento.getSectores();
        Asiento asiento;
        // Se puede agregar un batch size para trabajar con tamaños grandes
        for (Sector sector : sectores) {
            for (int i = 0; i < sector.getCapacidadTotal(); i++) {
                asiento = Asiento.builder()
                        .id(UUID.randomUUID())
                        .nroAsiento(String.valueOf(i))
                        .sector(sector)
                        .estaReservado(false)
                        .eventoId(eventoId)
                        .eventoNombre(evento.getNombre())
                        .usuario(usuarioId)
                        .build();
                asientos.add(asiento);
            }
        }
        asientoRepository.insert(asientos);
        return evento;
    }

    public void cancelarEvento(UUID id, UUID usuarioId) {
        Evento eventoExistente = obtenerEventoPorId(id);
        if (eventoExistente.getUsuario().equals(usuarioId)) {
            eventoExistente.setEstaActivo(false);
        } // baja lógica
        eventoRepository.save(eventoExistente);
    }

    public void eliminarEvento(UUID id, UUID usuarioId) {
        Evento eventoExistente = obtenerEventoPorId(id);
        if (eventoExistente.getUsuario().equals(usuarioId)) { // baja física
            eventoRepository.delete(eventoExistente);
        }
    }
}
