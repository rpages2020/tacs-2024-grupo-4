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
import java.util.ArrayList;
import java.util.List;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@Document
@Getter
@Setter
public class Evento {
    @Id
    private long id;
    private String nombre;
    private LocalDate fecha;
    private List<Ubicacion> ubicaciones;


    public Evento(String prueba, LocalDate fecha, List<Ubicacion> ubicaciones) {
    }
}
