package tacs.grupo_4.telegramBot;

import tacs.grupo_4.entities.*;

import java.util.List;

public class ImpresoraJSON {
    public static String imprimir(Estadisticas estadisticas) {
        return
                """     
                   Total Usuarios: %d
                   Total Eventos: %d
                   Total Recaudado: %f
                   Total Ventas: %d
                """.formatted(estadisticas.getTotalUsuarios(),
                        estadisticas.getTotalEventos(),
                        estadisticas.getTotalRecaudado(),
                        estadisticas.getTotalVentas());
    }

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
        if (evento.getUbicacion() == null) {
            return "";
        }
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
            if (evento.getUbicacion() == null) {
                return "";
            }
           response.append(imprimirEventoYEstadistica(evento));
        }
        return response.toString();
    }

    public static String imprimirEventoYEstadistica(Evento evento) {
        StringBuilder response = new StringBuilder();
        response.append(imprimir(evento));
        response.append("\n");
        response.append(evento.estadistica());
        response.append("\n\n");
        return response.toString();
    }



    public static String imprimir(Ticket ticket) {
        return
                """
                        Ticket
                            Evento : %s
                            Sector : %s
                            Número de Asiento: %s
                            Fecha: %s
                            Precio: %f
                        """.formatted(ticket.getEventoNombre(),
                        ticket.getSectorNombre(),
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
