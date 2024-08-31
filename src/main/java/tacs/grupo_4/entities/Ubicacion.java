package tacs.grupo_4.entities;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.ArrayList;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;


@Builder
@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "ubicaciones")
public class Ubicacion {
    @Id
    private Integer id;
    private String nombre;
    private Double precio;
    private Evento evento;
    private Integer capacidad;
    private Integer ticketsVendidos;
    private List<Reserva> reservas = new ArrayList<>();



    public Double precio() {
        return this.precio;
    }

//    public Boolean puedeRecibirReserva() {
//        return this.cantidadTickets > reservas.size();
//    }

//    public Reserva reservar(Usuario usuario) {
//        return this.puedeRecibirReserva() ? new Reserva(this, usuario) : null; // atajar esta execpcion
//    }

}