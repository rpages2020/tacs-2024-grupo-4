package tacs.grupo_4.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tacs.grupo_4.entities.Estadisticas;
import tacs.grupo_4.entities.Ticket;
import tacs.grupo_4.repositories.EventoRepository;
import tacs.grupo_4.services.EstadisticasService;

import java.util.UUID;

@Profile("api")
@RestController
@RequestMapping("/api/estadisticas")
public class EstadisticasController {
    @Autowired
    private EstadisticasService estadisticasService;

    @GetMapping()
    public ResponseEntity<Estadisticas> estadisticas() {
        return new ResponseEntity<>(estadisticasService.estadisticas(), HttpStatus.CREATED);
    }
}
