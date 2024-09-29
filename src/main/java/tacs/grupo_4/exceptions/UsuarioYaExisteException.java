package tacs.grupo_4.exceptions;

import tacs.grupo_4.entities.Usuario;

public class UsuarioYaExisteException extends RuntimeException{

    public UsuarioYaExisteException(Usuario usuario) {
        super("El usuario ya existe. Email: " + usuario.getEmail() + ". TelegramID: " + usuario.getTelegramUserId());
    }
}
