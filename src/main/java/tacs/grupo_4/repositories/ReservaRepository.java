package tacs.grupo_4.repositories;


import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import tacs.grupo_4.entities.Reserva;

@Repository
public interface ReservaRepository extends MongoRepository<Reserva, String> {
}
