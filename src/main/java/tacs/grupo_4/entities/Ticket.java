package tacs.grupo_4.entities;

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
@Document
public class Ticket {
    @Id
    private Long id;
    private Double precio;
    private Ubicacion ubicacion;
    private Reserva reserva;
    @Builder.Default
    private Boolean reservado = false;
    private Usuario usuario;


    public Double precio() { // se podirian manejar precios diferidos pero habria que verlos
        return this.precio == null ? this.ubicacion.precio() : this.precio;
    }

    public Boolean getReservado() {
        return this.reservado;
    }

    public void setReservado(Boolean booleano) {
        this.reservado = booleano;
    }
}
