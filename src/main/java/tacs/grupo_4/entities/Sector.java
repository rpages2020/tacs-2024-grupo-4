package tacs.grupo_4.entities;

import lombok.Builder;
import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.UUID;

@Builder
@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Document
//Sector nomás va a existir dentro del evento ----> neceitamos actualizarlo
public class Sector {   // Platea alta, Primera fila
    @Id
    private UUID id;
    private String nombre;
    private Integer capacidadTotal;
    private Integer reservas;
    private Double precio;
    //private String descripcion; //no se usa

    public String imprimite() {
        return "\n Nombre: " + nombre + "\n Tikcets Diponibles: " + capacidadRestante() + "\n Precio: " + precio + "\n";
    }

    public Double porcentajeVendido() {
        Double porcentaje = (reservas / (double) capacidadTotal) * 100;
        return porcentaje;
    }


    public Integer capacidadRestante() {
        return capacidadTotal - reservas;
    }

    public void sumarVenta() {
        this.reservas++;
    }

    public Double recaudacion() {
        return reservas * precio;
    }
}
