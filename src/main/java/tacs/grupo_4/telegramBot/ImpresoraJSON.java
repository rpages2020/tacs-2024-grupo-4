package tacs.grupo_4.telegramBot;

import tacs.grupo_4.entities.Evento;
import tacs.grupo_4.entities.Sector;
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
        StringBuilder mensaje = new StringBuilder();
                mensaje.append("""
                Datos del evento
                    Nombre: %s
                    Descripcion: %s
                    Fecha: %s
                    Ubicacion: %s
                    Capacidad: %d
                    Precio: %f
                    Estado: %s
                    Activo: %s
                    ID: %s
                   \s
                    Sectores:
               \s""".formatted(evento.getNombre(),
                evento.getDescripcion(),
                evento.getFecha(),
                evento.getUbicacion().getNombre(),
                evento.capacidad(),
                evento.getSectores().getFirst().getPrecio(),
                evento.getEstaConfirmado() ? "Confirmado" : "No confirmado",
                evento.getEstaActivo() ? "Activo" : "No activo",
                evento.getId()));

        for (Sector sector : evento.getSectores()) {
            mensaje.append(sector.imprimite());
        }

        return mensaje.toString();
    }

    public static String imprimirEventos(List<Evento> eventos) {
        StringBuilder response = new StringBuilder();
        for (Evento evento : eventos) {
            response.append(imprimir(evento));
            response.append("\n\n");
        }
        return response.toString();
    }
    public static String imprimirEventosYEstadisticas(List<Evento> eventos) {
        StringBuilder response = new StringBuilder();
        for (Evento evento : eventos) {
            response.append(imprimir(evento));
            response.append("\n");
            response.append(evento.estadistica());
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

    public static String imprimirTickets(List<Ticket> tickets) {
        StringBuilder response = new StringBuilder();
        for (Ticket ticket : tickets) {
            response.append(imprimir(ticket));
            response.append("\n\n");
        }
        return response.toString();
    }

}
