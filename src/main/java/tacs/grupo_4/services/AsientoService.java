package tacs.grupo_4.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import tacs.grupo_4.entities.Asiento;
import tacs.grupo_4.entities.Evento;
import tacs.grupo_4.entities.Sector;
import tacs.grupo_4.exceptions.AsientoNotFoundException;
import tacs.grupo_4.repositories.AsientoRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Profile("api")
@Service
public class AsientoService {
    @Autowired
    private AsientoRepository asientoRepository;

    public void liberarAsiento(UUID asientoId) {
        Asiento asiento = asientoRepository.findById(asientoId)
                .orElseThrow(AsientoNotFoundException::new);
        asiento.setEstaReservado(false);
        asientoRepository.save(asiento);
    }

    public void crearAsientos(Evento evento) {
        // Logica para que se pueda establecer el nroAsiento?
        List<Sector> sectores = evento.getSectores();
        sectores.forEach(sector -> {
            List<Asiento> asientos = new ArrayList<>();
            for (int i = 0; i < sector.getCapacidadTotal(); i++) {
                Asiento asiento = new Asiento(
                        UUID.randomUUID(),
                        "EJ-" + String.valueOf(i),
                        false,
                        null,
                        null,
                        sector,
                        evento.getId()
                );
                asientos.add(asiento);
            }
            asientoRepository.insert(asientos);
        });
    }
}
