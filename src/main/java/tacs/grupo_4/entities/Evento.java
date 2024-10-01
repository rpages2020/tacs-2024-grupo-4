package tacs.grupo_4.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@Document
@Getter
@Setter
public class Evento {
    @Id
    private UUID id;
    private String nombre;
    private LocalDateTime fecha;
    private Ubicacion ubicacion;
    private List<Sector> sectores;
    private Boolean estaConfirmado;
    private Boolean estaActivo;
    private String descripcion;
    private UUID usuario;


}
