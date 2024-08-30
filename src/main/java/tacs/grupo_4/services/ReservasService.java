package tacs.grupo_4.services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import tacs.grupo_4.entities.Ticket;
import tacs.grupo_4.dtos.TicketAddDTO;
import tacs.grupo_4.repositories.TicketRepository;

@Service
@Slf4j
public class ReservasService {

  private final TicketRepository ticketRepository;

  public ReservasService(TicketRepository ticketRepository) {
    this.ticketRepository = ticketRepository;
  }

  public void save(TicketAddDTO ticketAddDTO) {
    ticketRepository.save(Ticket.builder()
        .reservado(true)
        .build());
  }
}
