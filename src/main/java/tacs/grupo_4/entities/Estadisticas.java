package tacs.grupo_4.entities;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Estadisticas {
    private int mes;
    private Double totalRecaudado;
    private Long totalVentas;
    private Long totalUsuarios;
    private Long totalEventos;
}
