package tacs.grupo_4.controllers;

import tacs.grupo_4.entities.Ubicacion;
import tacs.grupo_4.services.UbicacionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;

import java.util.List;

@RestController
@RequestMapping("/api/ubicaciones")
public class UbicacionController {

    @Autowired
    private UbicacionService ubicacionServicio;

    @PostMapping
    public ResponseEntity<Ubicacion> crearUbicacion(@RequestBody Ubicacion ubicacion) {
        Ubicacion ubicacionCreada = ubicacionServicio.crearUbicacion(ubicacion);
        return new ResponseEntity<>(ubicacionCreada, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<Ubicacion>> obtenerTodasLasUbicaciones() {
        List<Ubicacion> ubicaciones = ubicacionServicio.obtenerTodasLasUbicaciones();
        return new ResponseEntity<>(ubicaciones, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Ubicacion> obtenerUbicacionPorId(@PathVariable String id) {
        Ubicacion ubicacion = ubicacionServicio.obtenerUbicacionPorId(id);
        return new ResponseEntity<>(ubicacion, HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Ubicacion> actualizarUbicacion(@PathVariable String id, @RequestBody Ubicacion ubicacion) {
        Ubicacion ubicacionActualizada = ubicacionServicio.actualizarUbicacion(id, ubicacion);
        return new ResponseEntity<>(ubicacionActualizada, HttpStatus.OK);
    }

}
