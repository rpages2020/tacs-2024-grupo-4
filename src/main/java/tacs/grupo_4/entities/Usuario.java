package tacs.grupo_4.entities;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;
import org.springframework.data.mongodb.core.index.Indexed;
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
    private UUID id;
    private String nombre;
    @Indexed(unique = true)
    private String email;
    @Indexed(unique = true)
    private long telegramUserId;
    @Indexed(unique = true)
    private int dni;
    @Builder.Default
    private String fechaAlta = formatFecha();
    @Builder.Default
    private boolean modoAdmin = false;
    @Version
    private Long version;

    private static String formatFecha() {
        LocalDateTime horaVenta = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yy");
        return horaVenta != null ? horaVenta.format(formatter) : null;
    }

}