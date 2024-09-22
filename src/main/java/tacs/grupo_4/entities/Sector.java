package tacs.grupo_4.entities;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;
import java.util.UUID;

@Builder
@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Document
public class Sector {   // Platea alta, Primera fila
    @Id
    private UUID id;
    private String nombre;
    private Long capacidad;
    private Double precio;
    private String descripcion;
}
