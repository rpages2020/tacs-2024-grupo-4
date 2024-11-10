package tacs.grupo_4.entities;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.time.LocalDateTime;
import java.util.UUID;

public class TicketTest {

    @Test
    public void testTicketBuilder() {
        UUID id = UUID.randomUUID();
        Asiento asiento = new Asiento();
        LocalDateTime horaVenta = LocalDateTime.now();

        Ticket ticket = Ticket.builder()
                .id(id)
                .precio(500.0)
                .asiento(asiento)
                .horaVenta(horaVenta)
                .estaActivo(true)
                .build();

        assertEquals(id, ticket.getId());
        assertEquals(500.0, ticket.getPrecio());
        assertEquals(asiento, ticket.getAsiento());
        assertEquals(horaVenta, ticket.getHoraVenta());
        assertTrue(ticket.isEstaActivo());
    }
}
