package tacs.grupo_4.repositories;

import org.springframework.context.annotation.Profile;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import tacs.grupo_4.entities.Ticket;
import java.util.List;
import java.util.UUID;

@Profile("api")
@Repository
public interface TicketRepository extends MongoRepository<Ticket, UUID> {
    List<Ticket> findByAsientoUsuario(UUID usuarioId);

    long countByFecha(String fecha);

    long countByFechaBetween(String fecha1, String fecha2);
}
