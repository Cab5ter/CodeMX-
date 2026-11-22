package mx.codemx.modules.usuarios.mapper;

import java.util.List;
import mx.codemx.modules.usuarios.Usuario;
import mx.codemx.modules.usuarios.domain.UsuarioDominio;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * Traduce entre la entidad JPA {@link Usuario} y el POJO de dominio {@link UsuarioDominio}.
 *
 * <p>MapStruct genera la implementación ({@code UsuarioMapperImpl}) en tiempo de compilación
 * y, gracias a {@code componentModel = "spring"}, la anota como {@code @Component}: queda
 * disponible en el contexto para inyectarla por constructor.
 *
 * <p>El identificador cambia de tipo en la frontera ({@code Long} en la entidad,
 * {@code Integer} en el dominio); MapStruct aplica la conversión numérica, con guarda de
 * nulos incluida.
 */
@Mapper(componentModel = "spring")
public interface UsuarioMapper {

    /** Entidad → dominio. {@code creadoEn} se expone como {@code fechaRegistro}. */
    @Mapping(source = "creadoEn", target = "fechaRegistro")
    UsuarioDominio toDominio(Usuario entidad);

    /**
     * Dominio → entidad.
     *
     * <p>Los {@code defaultValue} / {@code defaultExpression} evitan escribir {@code null}
     * en columnas declaradas {@code NOT NULL} cuando el cliente manda un JSON incompleto:
     * el mapeador siempre invoca todos los setters, así que sin ellos se perderían los
     * valores por defecto de la entidad.
     */
    @Mapping(source = "fechaRegistro", target = "creadoEn",
            defaultExpression = "java(java.time.Instant.now())")
    @Mapping(source = "nombre", target = "nombre", defaultValue = "")
    @Mapping(source = "email", target = "email", defaultValue = "")
    @Mapping(source = "passwordHash", target = "passwordHash", defaultValue = "")
    Usuario toEntity(UsuarioDominio dominio);

    /** Conversión de colecciones; MapStruct la deriva del método de un solo elemento. */
    List<UsuarioDominio> toDominio(List<Usuario> entidades);
}
