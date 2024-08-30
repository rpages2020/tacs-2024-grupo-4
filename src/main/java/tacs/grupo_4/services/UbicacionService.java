package tacs.grupo_4.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tacs.grupo_4.entities.Ubicacion;
import tacs.grupo_4.repositories.UbicacionRepository;

import java.util.List;

@Service
public class UbicacionService {

    @Autowired
    private UbicacionRepository ubicacionRepository;

    public Ubicacion crearUbicacion(Ubicacion ubicacion) {
        return ubicacionRepository.save(ubicacion);
    }

    public List<Ubicacion> obtenerTodasLasUbicaciones() {
        return ubicacionRepository.findAll();
    }

    public Ubicacion obtenerUbicacionPorId(String id) {
        return ubicacionRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Ubicación no encontrada"));
    }

    public void eliminarUbicacion(String id) {
        ubicacionRepository.deleteById(id);
    }

    public Ubicacion actualizarUbicacion(String id, Ubicacion ubicacionActualizada) {
        Ubicacion ubicacionExistente = obtenerUbicacionPorId(id);
        ubicacionExistente.setNombre(ubicacionActualizada.getNombre()); //TODO chequear que actualizan
        ubicacionExistente.setPrecio(ubicacionActualizada.getPrecio());
        return ubicacionRepository.save(ubicacionExistente);
    }
}
