package tacs.grupo_4.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import tacs.grupo_4.entities.Ticket;
import tacs.grupo_4.entities.Usuario;

import java.util.List;
import java.util.UUID;

@Repository
public interface TicketRepository extends MongoRepository<Ticket, UUID> {
    List<Ticket> findByAsientoUsuario (UUID usuarioId);
}
