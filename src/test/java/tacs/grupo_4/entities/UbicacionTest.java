package tacs.grupo_4.entities;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.UUID;

public class UbicacionTest {

    @Test
    public void testUbicacionBuilder() {
        UUID id = UUID.randomUUID();

        Ubicacion ubicacion = Ubicacion.builder()
                .id(id)
                .nombre("Estadio Y")
                .capacidad(50000L)
                .direccion("Calle 123")
                .build();

        assertEquals(id, ubicacion.getId());
        assertEquals("Estadio Y", ubicacion.getNombre());
        assertEquals(50000L, ubicacion.getCapacidad());
        assertEquals("Calle 123", ubicacion.getDireccion());
    }
}
