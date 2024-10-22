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
                .capacidad(100L)
                .precio(200.0)
                .descripcion("Primera fila")
                .build();

        assertEquals(id, sector.getId());
        assertEquals("Platea Alta", sector.getNombre());
        assertEquals(100L, sector.getCapacidad());
        assertEquals(200.0, sector.getPrecio());
        assertEquals("Primera fila", sector.getDescripcion());
    }
}
