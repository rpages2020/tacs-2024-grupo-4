package tacs.grupo_4.entities;

import lombok.Builder;
import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import java.util.UUID;

@Builder
@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
//@Document
//Sector nomás va a existir dentro del evento
public class Sector {   // Platea alta, Primera fila
    @Id
    private UUID id;
    private String nombre;
    private Long capacidad;
    private Double precio;
    private String descripcion;
}
