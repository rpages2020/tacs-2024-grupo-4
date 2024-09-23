package tacs.grupo_4.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import tacs.grupo_4.entities.Asiento;

import java.util.List;
import java.util.UUID;

@Repository
public interface AsientoRepository extends MongoRepository<Asiento, UUID> {
    List<Asiento> findByEventoIdAndSectorIdAndNroAsientoAndEstaReservado(UUID eventoId, UUID sectorId, String nroAsiento, Boolean estaReservado);
}
