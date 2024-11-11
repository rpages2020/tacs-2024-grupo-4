package tacs.grupo_4.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@Document
@Getter
@Setter
public class Evento {
    @Id
    private UUID id;
    private String nombre;
    private LocalDateTime fecha;
    private Ubicacion ubicacion;
    private List<Sector> sectores;
    private Boolean estaConfirmado;
    private Boolean estaActivo;
    private String descripcion;
    private UUID usuario;

    @Version
    private Long version;

    public Long capacidad() {
        Long capacidad = 0L;
        for (Sector sector : sectores) {
            if (sector.getCapacidadTotal() != null) {
                capacidad += sector.getCapacidadTotal();
            }
        }
        return capacidad;
    }

    public String estadistica() {
        StringBuilder estadistica = new StringBuilder();
        Double recaudacion = 0.0;
        Integer capacidadRestante = 0;
        for (Sector sector : sectores) {
            estadistica.append("\n").append(sector.getNombre()).append(":\n");
            Double recaudacionSector = sector.recaudacion();
            recaudacion += recaudacionSector;
            estadistica.append("Recaudación: ").append(recaudacionSector);
            Integer capacidadRestanteSector = sector.capacidadRestante();
            capacidadRestante = capacidadRestante + capacidadRestanteSector;
            estadistica.append("\nCapacidad Restante: ").append(capacidadRestanteSector);
            Double porcentajeVendidoSector = sector.porcentajeVendido();
            estadistica.append("\nPorcentaje vendido: ").append(porcentajeVendidoSector).append("%");

        }
        estadistica.append("\n\nTotales:\n");
        estadistica.append("\nRecaudacion: ").append(recaudacion);
        estadistica.append("\nCapacidad Restante: ").append(capacidadRestante);
        return estadistica.toString();
    }

    public void sumarVentaEnSector(String nombreSector) {
            for (Sector sector : sectores) {
                if (sector.getNombre().equals(nombreSector)) {
                    sector.sumarVenta();
                }
            }
    }
}
