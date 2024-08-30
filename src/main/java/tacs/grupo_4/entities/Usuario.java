package tacs.grupo_4.entities;


import java.util.List;
import java.util.ArrayList;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Builder
@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "usuarios")
public class Usuario {
    @Id
    private Integer id;
    private String nombre;
    private String email;
    private List<Ticket> tickets = new ArrayList();
    private List<Reserva> reservas = new ArrayList();


}