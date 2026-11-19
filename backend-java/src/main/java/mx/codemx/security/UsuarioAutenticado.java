package mx.codemx.security;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.security.Principal;

public record UsuarioAutenticado(long id, String email, String nombre) implements Principal {

    @Override
    @JsonIgnore
    public String getName() {
        return email;
    }
}
