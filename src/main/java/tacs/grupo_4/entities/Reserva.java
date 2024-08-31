package tacs.grupo_4.entities;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@Document
public class Reserva {
    @Id
    private Long id; //
    // private Ticket ticket; el ticket se instancia con la info correspondiente al momento de completar la compra
    private Usuario usuario;
    private LocalDate fecha;
    private String hora;
    private String descripcion;
    private List<Ticket> tickets;


    public Reserva(Ubicacion ubicacion, Usuario usuario) {
        this.usuario = usuario;
    }
}
