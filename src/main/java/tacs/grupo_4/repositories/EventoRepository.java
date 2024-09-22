package tacs.grupo_4.repositories;

import tacs.grupo_4.entities.Evento;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface EventoRepository extends MongoRepository<Evento, UUID> {
}
