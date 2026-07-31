package mx.codemx.modules.usuarios.mapper;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;
import mx.codemx.modules.usuarios.Usuario;
import mx.codemx.modules.usuarios.domain.UsuarioDominio;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    comments = "version: 1.6.3, compiler: javac, environment: Java 26.0.2 (Arch Linux)"
)
@Component
public class UsuarioMapperImpl implements UsuarioMapper {

    @Override
    public UsuarioDominio toDominio(Usuario entidad) {
        if ( entidad == null ) {
            return null;
        }

        UsuarioDominio usuarioDominio = new UsuarioDominio();

        usuarioDominio.setFechaRegistro( entidad.getCreadoEn() );
        if ( entidad.getId() != null ) {
            usuarioDominio.setId( entidad.getId().intValue() );
        }
        usuarioDominio.setNombre( entidad.getNombre() );
        usuarioDominio.setEmail( entidad.getEmail() );
        usuarioDominio.setPasswordHash( entidad.getPasswordHash() );

        return usuarioDominio;
    }

    @Override
    public Usuario toEntity(UsuarioDominio dominio) {
        if ( dominio == null ) {
            return null;
        }

        Usuario usuario = new Usuario();

        if ( dominio.getFechaRegistro() != null ) {
            usuario.setCreadoEn( dominio.getFechaRegistro() );
        }
        else {
            usuario.setCreadoEn( java.time.Instant.now() );
        }
        if ( dominio.getNombre() != null ) {
            usuario.setNombre( dominio.getNombre() );
        }
        else {
            usuario.setNombre( "" );
        }
        if ( dominio.getEmail() != null ) {
            usuario.setEmail( dominio.getEmail() );
        }
        else {
            usuario.setEmail( "" );
        }
        if ( dominio.getPasswordHash() != null ) {
            usuario.setPasswordHash( dominio.getPasswordHash() );
        }
        else {
            usuario.setPasswordHash( "" );
        }
        if ( dominio.getId() != null ) {
            usuario.setId( dominio.getId().longValue() );
        }

        return usuario;
    }

    @Override
    public List<UsuarioDominio> toDominio(List<Usuario> entidades) {
        if ( entidades == null ) {
            return null;
        }

        List<UsuarioDominio> list = new ArrayList<UsuarioDominio>( entidades.size() );
        for ( Usuario usuario : entidades ) {
            list.add( toDominio( usuario ) );
        }

        return list;
    }
}
