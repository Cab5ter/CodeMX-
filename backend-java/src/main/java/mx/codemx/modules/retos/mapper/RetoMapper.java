package mx.codemx.modules.retos.mapper;

import java.util.List;
import mx.codemx.modules.retos.Reto;
import mx.codemx.modules.retos.domain.RetoDominio;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * Traduce entre la entidad JPA {@link Reto} y el POJO de dominio {@link RetoDominio}.
 *
 * <p>MapStruct genera {@code RetoMapperImpl} al compilar y lo registra como componente de
 * Spring ({@code componentModel = "spring"}).
 */
@Mapper(componentModel = "spring")
public interface RetoMapper {

    /** Entidad → dominio. {@code creadoEn} se expone como {@code fechaCreacion}. */
    @Mapping(source = "creadoEn", target = "fechaCreacion")
    RetoDominio toDominio(Reto entidad);

    /**
     * Dominio → entidad. Los valores por defecto replican los de la entidad para no violar
     * las restricciones {@code NOT NULL} si llega un JSON incompleto.
     */
    @Mapping(source = "fechaCreacion", target = "creadoEn",
            defaultExpression = "java(java.time.Instant.now())")
    @Mapping(source = "titulo", target = "titulo", defaultValue = "")
    @Mapping(source = "descripcion", target = "descripcion", defaultValue = "")
    @Mapping(source = "dificultad", target = "dificultad", defaultValue = "BASICO")
    Reto toEntity(RetoDominio dominio);

    /** Conversión de colecciones; MapStruct la deriva del método de un solo elemento. */
    List<RetoDominio> toDominio(List<Reto> entidades);
}
