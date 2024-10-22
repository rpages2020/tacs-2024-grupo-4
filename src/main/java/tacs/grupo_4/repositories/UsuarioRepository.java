package tacs.grupo_4.repositories;

import org.springframework.context.annotation.Profile;
import tacs.grupo_4.entities.Usuario;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;


import java.util.Optional;
import java.util.UUID;

@Profile("api")
@Repository
public interface UsuarioRepository extends MongoRepository<Usuario, UUID> {
    Optional<Usuario> findByTelegramUserId(Long id);
}
