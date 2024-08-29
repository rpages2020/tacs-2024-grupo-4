package tac.grupo_4.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import tac.grupo_4.services.ReservasService;
import tac.grupo_4.dtos.TicketAddDTO;


@RestController
@RequestMapping("/v1/reservas")
@Slf4j
public class ReservasController {

  private final ReservasService reservasService;

  public ReservasController(ReservasService reservasService) {
    this.reservasService = reservasService;
  }

  @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
  @ResponseStatus(value = HttpStatus.OK)
  public void tickets()  {
    log.info("Anda esto");
  }

  @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
  @ResponseStatus(value = HttpStatus.OK)
  public void save(@RequestBody TicketAddDTO ticketAddDTO)  {
    reservasService.save(ticketAddDTO);
  }
}
