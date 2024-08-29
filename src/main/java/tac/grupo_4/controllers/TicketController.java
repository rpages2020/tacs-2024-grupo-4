package tac.grupo_4.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/v1/tickets")
@Slf4j
public class TicketController {

  @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
  @ResponseStatus(value = HttpStatus.OK)
  public void tickets()  {
    log.info("Anda esto");
  }
}
