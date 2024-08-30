package tacs.grupo_4.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import tacs.grupo_4.entities.Ticket;

@Repository
public interface TicketRepository extends MongoRepository<Ticket, Long> {
}