package tacs.grupo_4.entities;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.query.FluentQuery;
import tacs.grupo_4.repositories.EventoRepository;
import tacs.grupo_4.services.EventoService;

import static org.junit.jupiter.api.Assertions.*;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;
import java.util.List;
import java.util.ArrayList;
import java.util.function.Function;

public class EventoTest {

    @Test
    public void testEventoBuilder() {
        UUID id = UUID.randomUUID();
        Ubicacion ubicacion = new Ubicacion();
        List<Sector> sectores = new ArrayList<>();
        LocalDateTime fecha = LocalDateTime.now();
        UUID usuarioId = UUID.randomUUID();

        Evento evento = Evento.builder()
                .id(id)
                .nombre("Concierto X")
                .fecha(fecha)
                .ubicacion(ubicacion)
                .sectores(sectores)
                .estaConfirmado(true)
                .estaActivo(true)
                .descripcion("Gran concierto")
                .usuario(usuarioId)
                .build();

        assertEquals(id, evento.getId());
        assertEquals("Concierto X", evento.getNombre());
        assertEquals(fecha, evento.getFecha());
        assertEquals(ubicacion, evento.getUbicacion());
        assertEquals(sectores, evento.getSectores());
        assertTrue(evento.getEstaConfirmado());
        assertTrue(evento.getEstaActivo());
        assertEquals("Gran concierto", evento.getDescripcion());
        assertEquals(usuarioId, evento.getUsuario());
    }

}
