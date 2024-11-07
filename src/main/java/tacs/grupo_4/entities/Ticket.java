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
import java.util.UUID;

@Builder
@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Document
public class Ticket {
    @Id
    private UUID id;
    private Double precio;
    private Asiento asiento;
    private LocalDateTime horaVenta;
    private boolean estaActivo;
    private String eventoNombre;
    private String sectorNombre;

}
