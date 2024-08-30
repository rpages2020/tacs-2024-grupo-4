package tacs.grupo_4.repositories;

import tacs.grupo_4.entities.Ubicacion;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UbicacionRepository extends MongoRepository<Ubicacion, String> {
}
