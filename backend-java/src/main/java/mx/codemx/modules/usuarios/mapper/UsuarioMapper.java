package mx.codemx.modules.usuarios.mapper;

import java.util.List;
import mx.codemx.modules.usuarios.Usuario;
import mx.codemx.modules.usuarios.domain.UsuarioDominio;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UsuarioMapper {

    @Mapping(source = "creadoEn", target = "fechaRegistro")
    UsuarioDominio toDominio(Usuario entidad);

    @Mapping(source = "fechaRegistro", target = "creadoEn",
            defaultExpression = "java(java.time.Instant.now())")
    @Mapping(source = "nombre", target = "nombre", defaultValue = "")
    @Mapping(source = "email", target = "email", defaultValue = "")
    @Mapping(source = "passwordHash", target = "passwordHash", defaultValue = "")
    Usuario toEntity(UsuarioDominio dominio);

    List<UsuarioDominio> toDominio(List<Usuario> entidades);
}
