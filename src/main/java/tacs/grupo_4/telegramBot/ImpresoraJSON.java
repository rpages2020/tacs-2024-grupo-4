package tacs.grupo_4.telegramBot;

import tacs.grupo_4.entities.Evento;
import tacs.grupo_4.entities.Ticket;
import tacs.grupo_4.entities.Usuario;

import java.util.List;

public class ImpresoraJSON {
    public static String imprimir(Usuario usuario) {
        return
                """
                Datos de usuario
                    Nombre: %s
                    Email: %s
                    DNI: %s
                """.formatted(usuario.getNombre(),
                        usuario.getEmail(),
                        usuario.getDni());
    }

    public static String imprimir(Evento evento) {
        if (evento.getSectores().isEmpty()) {
            return "El evento no tiene sectores disponibles.";
        }
        return
                """
                Datos del evento
                    Nombre: %s
                    Descripcion: %s
                    Fecha: %s
                    Ubicacion: %s
                    Capacidad: %d
                    Precio: %f
                    Estado: %s
                    ID: %s
                    SectorId: %s
                """.formatted(evento.getNombre(),
                        evento.getDescripcion(),
                        evento.getFecha(),
                        evento.getUbicacion().getNombre(),
                        evento.getSectores().get(0).getCapacidad(),  // Cambiado a get(0)
                        evento.getSectores().get(0).getPrecio(),    // Cambiado a get(0)
                        evento.getEstaConfirmado() ? "Confirmado" : "No confirmado",
                        evento.getId(),
                        evento.getSectores().get(0).getId());       // Cambiado a get(0)
    }

    public static String imprimir(List<Evento> eventos) {
        StringBuilder response = new StringBuilder();
        for (Evento evento : eventos) {
            response.append(imprimir(evento));
            response.append("\n\n");
        }
        return response.toString();
    }

    public static String imprimir(Ticket ticket) {
        return
                """
                Ticket
                    Evento ID: %s
                    Número de Asiento: %s
                    Fecha: %s
                    Precio: %f
                """.formatted(ticket.getAsiento().getEventoId(),
                        ticket.getAsiento().getNroAsiento(),
                        ticket.getHoraVenta(),
                        ticket.getPrecio());
    }
}
