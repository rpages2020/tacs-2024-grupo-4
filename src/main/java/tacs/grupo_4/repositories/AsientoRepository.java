package tacs.grupo_4.repositories;

import org.springframework.context.annotation.Profile;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import tacs.grupo_4.entities.Asiento;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Profile("api")
@Repository
public interface AsientoRepository extends MongoRepository<Asiento, UUID> {
    List<Asiento> findByEventoIdAndSectorIdAndNroAsientoAndEstaReservado(UUID eventoId, UUID sectorId, String nroAsiento, Boolean estaReservado);
    Optional<Asiento> findFirstByEstaReservadoAndEventoIdAndSectorNombre(boolean estaReservado, UUID eventoId, String sectorNombre);

    List<Asiento> findByEventoIdAndSectorNombreAndNroAsientoAndEstaReservado(UUID eventoId, String sectorNombre, String nroAsiento, boolean b);
}
