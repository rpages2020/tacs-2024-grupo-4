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
import tacs.grupo_4.entities.Sector;
import tacs.grupo_4.exceptions.AsientoNotFoundException;
import tacs.grupo_4.repositories.AsientoRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

class AsientoServiceTest {

    @Mock
    private AsientoRepository asientoRepository;

    @InjectMocks
    private AsientoService asientoService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testLiberarAsientoSuccess() {
        UUID asientoId = UUID.randomUUID();
        Asiento asiento = new Asiento();
        asiento.setId(asientoId);
        asiento.setEstaReservado(true);

        when(asientoRepository.findById(asientoId)).thenReturn(Optional.of(asiento));

        asientoService.liberarAsiento(asientoId);

        assertFalse(asiento.getEstaReservado());
        verify(asientoRepository).save(asiento);
    }

    @Test
    void testLiberarAsientoNotFound() {
        UUID asientoId = UUID.randomUUID();
        when(asientoRepository.findById(asientoId)).thenReturn(Optional.empty());

        assertThrows(AsientoNotFoundException.class, () -> asientoService.liberarAsiento(asientoId));
    }

    @Test
    void testCrearAsientos() {
        Evento evento = new Evento();
        evento.setId(UUID.randomUUID());
        Sector sector = new Sector();
        sector.setCapacidad(5L);
        List<Sector> sectores = List.of(sector);
        evento.setSectores(sectores);

        asientoService.crearAsientos(evento);

        verify(asientoRepository).insert(anyList());
    }
}
