package tacs.grupo_4.entities;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.UUID;

public class UsuarioTest {

    @Test
    public void testUsuarioBuilder() {
        UUID id = UUID.randomUUID();

        Usuario usuario = Usuario.builder()
                .id(id)
                .nombre("Juan Perez")
                .email("juan.perez@example.com")
                .telegramUserId(123456789L)
                .dni(12345678)
                .build();

        assertEquals(id, usuario.getId());
        assertEquals("Juan Perez", usuario.getNombre());
        assertEquals("juan.perez@example.com", usuario.getEmail());
        assertEquals(123456789L, usuario.getTelegramUserId());
        assertEquals(12345678, usuario.getDni());
    }
}
