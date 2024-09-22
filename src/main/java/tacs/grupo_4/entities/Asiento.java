package tacs.grupo_4.entities;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.UUID;

@Builder
@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Document
public class Asiento {      // Butaca B25, Persona 14 en sector X
    @Id
    private UUID id;
    private String nroAsiento;
    private Boolean estaReservado;
    private UUID usuario;
    private LocalDateTime reservadoEn;
    private Sector sector;
    private UUID eventoId;
}
