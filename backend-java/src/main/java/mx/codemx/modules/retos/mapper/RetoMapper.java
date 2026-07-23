package mx.codemx.modules.retos.mapper;

import java.util.List;
import mx.codemx.modules.retos.Reto;
import mx.codemx.modules.retos.domain.RetoDominio;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface RetoMapper {

    @Mapping(source = "creadoEn", target = "fechaCreacion")
    RetoDominio toDominio(Reto entidad);

    @Mapping(source = "fechaCreacion", target = "creadoEn",
            defaultExpression = "java(java.time.Instant.now())")
    @Mapping(target = "casos", ignore = true)
    @Mapping(source = "titulo", target = "titulo", defaultValue = "")
    @Mapping(source = "descripcion", target = "descripcion", defaultValue = "")
    @Mapping(source = "dificultad", target = "dificultad", defaultValue = "BASICO")
    Reto toEntity(RetoDominio dominio);

    List<RetoDominio> toDominio(List<Reto> entidades);
}
