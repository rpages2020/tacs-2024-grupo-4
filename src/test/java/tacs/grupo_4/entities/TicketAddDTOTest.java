package tacs.grupo_4.entities;

import org.junit.jupiter.api.Test;
import tacs.grupo_4.dtos.TicketAddDTO;

import static org.junit.jupiter.api.Assertions.*;

public class TicketAddDTOTest {

    @Test
    public void testTicketAddDTOBuilder() {
        TicketAddDTO ticketAddDTO = TicketAddDTO.builder()
                .id(1)
                .description("Entrada General")
                .build();

        assertEquals(1, ticketAddDTO.getId());
        assertEquals("Entrada General", ticketAddDTO.getDescription());
    }
}
