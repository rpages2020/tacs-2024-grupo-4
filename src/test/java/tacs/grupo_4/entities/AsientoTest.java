package tacs.grupo_4.entities;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.time.LocalDateTime;
import java.util.UUID;

public class AsientoTest {

    @Test
    public void testAsientoBuilder() {
        UUID id = UUID.randomUUID();
        UUID usuarioId = UUID.randomUUID();
        Sector sector = new Sector();
        UUID eventoId = UUID.randomUUID();
        LocalDateTime reservadoEn = LocalDateTime.now();

        Asiento asiento = Asiento.builder()
                .id(id)
                .nroAsiento("B25")
                .estaReservado(true)
                .usuario(usuarioId)
                .reservadoEn(reservadoEn)
                .sector(sector)
                .eventoId(eventoId)
                .build();

        assertEquals(id, asiento.getId());
        assertEquals("B25", asiento.getNroAsiento());
        assertTrue(asiento.getEstaReservado());
        assertEquals(usuarioId, asiento.getUsuario());
        assertEquals(reservadoEn, asiento.getReservadoEn());
        assertEquals(sector, asiento.getSector());
        assertEquals(eventoId, asiento.getEventoId());
    }
}
