package tacs.grupo_4.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import tacs.grupo_4.entities.Asiento;
import tacs.grupo_4.entities.Evento;
import tacs.grupo_4.exceptions.AsientoNotFoundException;
import tacs.grupo_4.repositories.AsientoRepository;
import tacs.grupo_4.repositories.EventoRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

class EventoServiceTest {

    @Mock
    private EventoRepository eventoRepository;

    @Mock
    private AsientoRepository asientoRepository;

    @InjectMocks
    private EventoService eventoService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCrearEvento() {
        Evento evento = new Evento();
        when(eventoRepository.save(evento)).thenReturn(evento);

        Evento createdEvento = eventoService.crearEvento(evento);

        assertNotNull(createdEvento);
        verify(eventoRepository).save(evento);
    }

    @Test
    void testObtenerEventoPorIdSuccess() {
        UUID id = UUID.randomUUID();
        Evento evento = new Evento();
        evento.setId(id);

        when(eventoRepository.findById(id)).thenReturn(Optional.of(evento));

        Evento foundEvento = eventoService.obtenerEventoPorId(id);

        assertNotNull(foundEvento);
        assertEquals(id, foundEvento.getId());
    }

    @Test
    void testObtenerEventoPorIdNotFound() {
        UUID id = UUID.randomUUID();
        when(eventoRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> eventoService.obtenerEventoPorId(id));
    }

    @Test
    void testReservarAsientoSuccess() {
        UUID eventoId = UUID.randomUUID();
        UUID sectorId = UUID.randomUUID();
        String nroAsiento = "A1";
        UUID usuario = UUID.randomUUID();
        Asiento asiento = new Asiento();
        asiento.setId(UUID.randomUUID());
        asiento.setEstaReservado(false); // Asegúrate de que inicialmente no esté reservado

        // Mock del repositorio para buscar el asiento
        when(asientoRepository.findByEventoIdAndSectorIdAndNroAsientoAndEstaReservado(eventoId, sectorId, nroAsiento, false))
                .thenReturn(List.of(asiento));

        // Mock para que el método save devuelva el asiento después de guardarlo
        when(asientoRepository.save(asiento)).thenReturn(asiento);

        // Ejecutamos el método que queremos probar
        Asiento reservedAsiento = eventoService.reservarAsiento(eventoId, sectorId, nroAsiento, usuario);

        // Verificamos que el asiento no sea nulo
        assertNotNull(reservedAsiento, "El asiento reservado no debe ser nulo");

        // Verificamos que el asiento fue correctamente reservado y asignado al usuario
        assertTrue(reservedAsiento.getEstaReservado(), "El asiento debe estar marcado como reservado");
        assertEquals(usuario, reservedAsiento.getUsuario(), "El asiento debe estar asignado al usuario correcto");

        // Verificamos que el repositorio haya guardado los cambios
        verify(asientoRepository).save(asiento);
    }

    @Test
    void testReservarAsientoNotFound() {
        UUID eventoId = UUID.randomUUID();
        UUID sectorId = UUID.randomUUID();
        String nroAsiento = "A1";

        when(asientoRepository.findByEventoIdAndSectorIdAndNroAsientoAndEstaReservado(eventoId, sectorId, nroAsiento, false))
                .thenReturn(List.of());

        assertThrows(AsientoNotFoundException.class, () -> eventoService.reservarAsiento(eventoId, sectorId, nroAsiento, UUID.randomUUID()));
    }

    @Test
    void testReservarAsientoSuccess2() {
        UUID eventoId = UUID.randomUUID();
        UUID sectorId = UUID.randomUUID();
        String nroAsiento = "A1";
        UUID usuario = UUID.randomUUID();
        Asiento asiento = new Asiento();
        asiento.setId(UUID.randomUUID());
        asiento.setEstaReservado(false); // Asegúrate de que inicialmente no esté reservado

        // Mock del repositorio para buscar el asiento
        when(asientoRepository.findByEventoIdAndSectorIdAndNroAsientoAndEstaReservado(eventoId, sectorId, nroAsiento, false))
                .thenReturn(List.of(asiento));

        // Mock para que el método save devuelva el asiento después de guardarlo
        when(asientoRepository.save(asiento)).thenReturn(asiento);

        // Ejecutamos el método que queremos probar
        Asiento reservedAsiento = eventoService.reservarAsiento(eventoId, sectorId, nroAsiento, usuario);

        // Verificamos que el asiento no sea nulo
        assertNotNull(reservedAsiento, "El asiento reservado no debe ser nulo");

        // Verificamos que el asiento fue correctamente reservado y asignado al usuario
        assertTrue(reservedAsiento.getEstaReservado(), "El asiento debe estar marcado como reservado");
        assertEquals(usuario, reservedAsiento.getUsuario(), "El asiento debe estar asignado al usuario correcto");

        // Verificamos que el repositorio haya guardado los cambios
        verify(asientoRepository).save(asiento);
    }
    @Test
    void testReservarAsientoThrowsAsientoNotFoundException() {
        UUID eventoId = UUID.randomUUID();
        UUID sectorId = UUID.randomUUID();
        String nroAsiento = "A1";
        UUID usuario = UUID.randomUUID();

        // Mock para devolver una lista vacía, simulando que no se encuentra el asiento
        when(asientoRepository.findByEventoIdAndSectorIdAndNroAsientoAndEstaReservado(eventoId, sectorId, nroAsiento, false))
                .thenReturn(List.of());

        // Ejecutamos el método que queremos probar y verificamos que se lance la excepción
        assertThrows(AsientoNotFoundException.class, () -> {
            eventoService.reservarAsiento(eventoId, sectorId, nroAsiento, usuario);
        });

        // Verificamos que no se intente guardar nada en el repositorio, ya que no se encontró el asiento
        verify(asientoRepository, never()).save(any());
    }

}
