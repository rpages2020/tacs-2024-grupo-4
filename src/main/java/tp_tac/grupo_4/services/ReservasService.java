package tp_tac.grupo_4.services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import tp_tac.grupo_4.repositories.TicketRepository;

@Service
@Slf4j
public class ReservasService {

    private final TicketRepository ticketRepository;

  public ReservasService(TicketRepository ticketRepository) {
    this.ticketRepository = ticketRepository;
  }
}
