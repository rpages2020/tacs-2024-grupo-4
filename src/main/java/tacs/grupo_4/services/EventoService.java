package tacs.grupo_4.services;

import org.springframework.context.annotation.Profile;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.transaction.annotation.Transactional;
import tacs.grupo_4.entities.Asiento;
import tacs.grupo_4.entities.Evento;
import tacs.grupo_4.entities.Sector;
import tacs.grupo_4.entities.Usuario;
import tacs.grupo_4.exceptions.AsientoNotFoundException;
import tacs.grupo_4.exceptions.EventoNotFoundException;
import tacs.grupo_4.repositories.AsientoRepository;
import tacs.grupo_4.repositories.EventoRepository;
import org.springframework.stereotype.Service;
import tacs.grupo_4.repositories.UsuarioRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Profile("api")
@Service
public class EventoService {


    private final EventoRepository eventoRepository;
    private final AsientoRepository asientoRepository;
    private final MongoTemplate asientoTemplate;
    private final UsuarioRepository usuarioRepository;

    public EventoService(EventoRepository eventoRepository, AsientoRepository asientoRepository, MongoTemplate asientoTemplate, UsuarioRepository usuarioRepository) {
        this.eventoRepository = eventoRepository;
        this.asientoRepository = asientoRepository;
        this.asientoTemplate = asientoTemplate; // abstracción de menor nivel que el repository
        this.usuarioRepository = usuarioRepository;
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

    @Transactional
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
        Optional<Asiento> asientoOpt = asientoRepository.findByEventoIdAndSectorNombreAndNroAsientoAndEstaReservado(eventoId, sectorNombre, nroAsiento, false)
                .stream().findFirst();

        if (!asientoOpt.isPresent()) {
            throw new AsientoNotFoundException("No hay asientos disponibles en el sector o el asiento ya está reservado.");
        }

        Asiento asiento = asientoOpt.get();
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
        /* Opción que con repository
        Asiento asiento = asientoRepository.findFirstByEstaReservadoAndEventoIdAndSectorNombre(false, evento, sectorNombre)
                .orElseThrow(AsientoNotFoundException::new);
        asiento.setEstaReservado(true);
        asiento.setUsuario(usuario);
        asiento.setReservadoEn(LocalDateTime.now());
        this.sumarReserva(evento, sectorNombre);
        return asientoRepository.save(asiento);
         */
        Query query = new Query(
                Criteria.where("eventoId").is(evento)
                        .and("estaReservado").is(false)
                        .and("sector.nombre").is(sectorNombre)
        );
        Update update = new Update()
                .set("estaReservado", true)
                .set("usuario", usuario)
                .set("reservadoEn", LocalDateTime.now());

        Asiento asiento = asientoTemplate.findAndModify(query, update, Asiento.class);
        if (asiento != null) {
            this.sumarReserva(evento, sectorNombre);
        } else {
            throw new AsientoNotFoundException("");
        }

        return asiento;
    }

    public void sumarReserva(UUID eventoId, String nombreSector) {
        Evento evento = eventoRepository.findById(eventoId).orElseThrow(() -> new RuntimeException("Evento no encontrado"));
        evento.sumarVentaEnSector(nombreSector);
        eventoRepository.save(evento);
    }

    @Transactional
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

    @Transactional
    public void cancelarEvento(UUID id, long usuarioId) {
        Evento evento = eventoRepository.findById(id)
                .orElseThrow(EventoNotFoundException::new);
        Usuario usuario = usuarioRepository.findByTelegramUserId(usuarioId).get();
        if (usuario.getId().equals(evento.getUsuario()) || usuario.isModoAdmin()) {
            evento.setEstaActivo(false);
            eventoRepository.save(evento);
        } else {
            throw new RuntimeException("Acceso denegado.");
        }
    }

    @Transactional
    public void eliminarEvento(UUID id, UUID usuarioId) {
        Evento evento = obtenerEventoPorId(id);
        Usuario usuario = usuarioRepository.findById(evento.getUsuario()).get();
        if (usuarioId.equals(evento.getUsuario()) || usuario.isModoAdmin()) {
            eventoRepository.delete(evento);
        } else {
            throw new RuntimeException("Acceso denegado.");
        }
    }

    @Transactional
    public void cambiarIdUsuario(String id) {
        Evento evento = obtenerEventoPorId(UUID.fromString(id));
        evento.setUsuario(UUID.randomUUID());
        eventoRepository.save(evento);
    }

    public long cantidadEventosPorFecha(String fecha) {
        return eventoRepository.countByFechaCreacion(fecha);
    }
    public long cantidadAltasEntreFechas(String fecha1, String fecha2) {
        return  eventoRepository.countByFechaCreacionBetween(fecha1, fecha2);
    }
}
