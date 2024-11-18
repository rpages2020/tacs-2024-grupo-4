package tacs.grupo_4.repositories.DataInitializer;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import tacs.grupo_4.entities.Usuario;
import tacs.grupo_4.entities.Asiento;
import tacs.grupo_4.entities.Ticket;
import tacs.grupo_4.entities.Sector;
import tacs.grupo_4.entities.Evento;
import tacs.grupo_4.entities.Ubicacion;
import tacs.grupo_4.repositories.AsientoRepository;
import tacs.grupo_4.repositories.UsuarioRepository;
import tacs.grupo_4.repositories.EventoRepository;
import tacs.grupo_4.repositories.TicketRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.Arrays;


@Profile("api")
@Component
public class DataInitializer implements CommandLineRunner {

    private final UsuarioRepository usuarioRepository;
    private final EventoRepository eventoRepository;
    private final TicketRepository ticketRepository;
    private final AsientoRepository asientoRepository;

    public DataInitializer(UsuarioRepository usuarioRepository, EventoRepository eventoRepository, TicketRepository ticketRepository, AsientoRepository asientoRepository) {
        this.usuarioRepository = usuarioRepository;
        this.eventoRepository = eventoRepository;
        this.ticketRepository = ticketRepository;
        this.asientoRepository = asientoRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        usuarioRepository.deleteAll();
        eventoRepository.deleteAll();
        ticketRepository.deleteAll();
        System.out.println("Iniciando carga de datos...");
        initializeUsuarios();
        initializeEventos();
        initializeTickets();
        System.out.println("Datos iniciales cargados en la base de datos.");
    }

    private void initializeUsuarios() {
        System.out.println("initializeUsuarios carga de datos...");

            List<Usuario> usuarios = new ArrayList<>();

            // Crear 2 Admins
            for (int i = 1; i <= 2; i++) {
                Usuario admin = Usuario.builder()
                        .id(UUID.randomUUID())
                        .nombre("Admin " + i)
                        .email("admin" + i + "@example.com")
                        .telegramUserId(100000L + i)
                        .dni(100000 + i)
                        .modoAdmin(true)
                        .build();
                usuarioRepository.save(admin);
                usuarios.add(admin);
            }

            // Crear 4 usuarios normales
            for (int i = 1; i <= 4; i++) {
                Usuario usuario = Usuario.builder()
                        .id(UUID.randomUUID())
                        .nombre("Usuario " + i)
                        .email("usuario" + i + "@example.com")
                        .telegramUserId(200000L + i)
                        .dni(200000 + i)
                        .modoAdmin(false)
                        .build();
                usuarioRepository.save(usuario);
                usuarios.add(usuario);
            }

            //usuarioRepository.saveAll(usuarios);

    }

    private void initializeEventos() {
        System.out.println("initializeEventos carga de datos...");

        // Obtener usuarios para asignar a los eventos
        List<Usuario> users = usuarioRepository.findAll();
        Random r =  new Random();
        List<Evento> eventos = new ArrayList<>();

        List<Ubicacion> ubicaciones = Arrays.asList(
                new Ubicacion(UUID.randomUUID(), "Estadio Monumental", 83000L, "Av. Figueroa Alcorta 7597, Buenos Aires, Argentina"),
                new Ubicacion(UUID.randomUUID(), "Teatro Colón", 2500L, "Cerrito 628, Buenos Aires, Argentina"),
                new Ubicacion(UUID.randomUUID(), "Arena Ciudad de México", 22000L, "Av. Conscripto 3110, Ciudad de México, México")
        );

        List<String> nombresEventos = Arrays.asList(
                "Concierto de Coldplay",
                "Obra de teatro: Hamlet",
                "Fútbol: Final Copa Libertadores 2024",
                "Festival Internacional de Jazz",
                "Conferencia de Tecnología Web",
                "Expo Internacional de Innovación",
                "Gran Show de Circo"
        );

        for (int i = 0; i < 7; i++) {
            Usuario usuario = users.get(r.nextInt(0, (int) usuarioRepository.count() - 1));

            // Selección aleatoria de ubicación y nombre de evento
            Ubicacion ubicacion = ubicaciones.get(r.nextInt(ubicaciones.size()));
            String nombreEvento = nombresEventos.get(r.nextInt(nombresEventos.size()));

            // Crear sectores
            Sector sector1 = Sector.builder()
                    .id(UUID.randomUUID())
                    .nombre("Sector " + (i) + " A")
                    .capacidadTotal(100)
                    .reservas(0)
                    .precio(1000.0 + (i * 100))
                    .build();

            Sector sector2 = Sector.builder()
                    .id(UUID.randomUUID())
                    .nombre("Sector " + (i) + " B")
                    .capacidadTotal(150)
                    .reservas(0)
                    .precio(1500.0 + (i * 200))
                    .build();

            // Crear evento
            Evento evento = Evento.builder()
                    .id(UUID.randomUUID())
                    .nombre(nombreEvento)
                    .fecha(LocalDateTime.of(2024, 12, i + 1, 20, 0))
                    .descripcion("Descripción del evento " + i)
                    .usuario(usuario.getId())
                    .estaConfirmado(true)
                    .ubicacion(ubicacion)
                    .estaActivo(true)
                    .sectores(Arrays.asList(sector1, sector2))
                    .build();

            eventos.add(evento);
        }

        eventoRepository.saveAll(eventos);
    }

    private void initializeTickets() {
        System.out.println("initializeTickets carga de datos...");
            // Obtener eventos y usuarios
        Random r = new Random();
            List<Evento> eventos = eventoRepository.findAll();
            List<Usuario> usuarios = usuarioRepository.findAll().subList(2, 6); // 4 usuarios normales

            List<Ticket> tickets = new ArrayList<>();
            int ticketId = 1;

            for (Evento evento : eventos) {
                for (Sector sector : evento.getSectores()) {
                    // Crear asientos para cada evento y sector
                    for (int i = 1; i <= sector.getCapacidadTotal(); i++) {
                        Asiento asiento = Asiento.builder()
                                .id(UUID.randomUUID())
                                .nroAsiento(sector.getNombre() + " - " + i)
                                .estaReservado(false)
                                .usuario(usuarios.get(r.nextInt(0, usuarios.size() - 1)).getId())
                                .reservadoEn(LocalDateTime.now())
                                .sector(sector)
                                .eventoId(evento.getId())
                                .eventoNombre(evento.getNombre())
                                .build();

                        asientoRepository.save(asiento);

                        // Crear tickets asignados a los asientos
                        if (ticketId <= 30) { // Generamos solo 30 tickets
                            Ticket ticket = Ticket.builder()
                                    .id(UUID.randomUUID())
                                    .precio(sector.getPrecio())
                                    .asiento(asiento)
                                    .horaVenta(LocalDateTime.now())
                                    .estaActivo(true)
                                    .eventoNombre(evento.getNombre())
                                    .sectorNombre(sector.getNombre())
                                    .build();

                            tickets.add(ticket);
                            ticketId++;
                        }
                    }
                }


            ticketRepository.saveAll(tickets);
        }
    }
}