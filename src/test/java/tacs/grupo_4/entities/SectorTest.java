package tacs.grupo_4.entities;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.UUID;

public class SectorTest {

    @Test
    public void testSectorBuilder() {
        UUID id = UUID.randomUUID();

        Sector sector = Sector.builder()
                .id(id)
                .nombre("Platea Alta")
                .capacidadTotal(100)
                .precio(200.0)
                .build();

        assertEquals(id, sector.getId());
        assertEquals("Platea Alta", sector.getNombre());
        assertEquals(100, sector.getCapacidadTotal());
        assertEquals(200.0, sector.getPrecio());
    }
}
