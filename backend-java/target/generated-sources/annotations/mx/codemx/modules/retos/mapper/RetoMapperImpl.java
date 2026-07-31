package mx.codemx.modules.retos.mapper;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;
import mx.codemx.modules.retos.Dificultad;
import mx.codemx.modules.retos.Reto;
import mx.codemx.modules.retos.domain.RetoDominio;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    comments = "version: 1.6.3, compiler: javac, environment: Java 26.0.2 (Arch Linux)"
)
@Component
public class RetoMapperImpl implements RetoMapper {

    @Override
    public RetoDominio toDominio(Reto entidad) {
        if ( entidad == null ) {
            return null;
        }

        RetoDominio retoDominio = new RetoDominio();

        retoDominio.setFechaCreacion( entidad.getCreadoEn() );
        if ( entidad.getId() != null ) {
            retoDominio.setId( entidad.getId().intValue() );
        }
        retoDominio.setTitulo( entidad.getTitulo() );
        retoDominio.setDescripcion( entidad.getDescripcion() );
        retoDominio.setDificultad( entidad.getDificultad() );

        return retoDominio;
    }

    @Override
    public Reto toEntity(RetoDominio dominio) {
        if ( dominio == null ) {
            return null;
        }

        Reto reto = new Reto();

        if ( dominio.getFechaCreacion() != null ) {
            reto.setCreadoEn( dominio.getFechaCreacion() );
        }
        else {
            reto.setCreadoEn( java.time.Instant.now() );
        }
        if ( dominio.getTitulo() != null ) {
            reto.setTitulo( dominio.getTitulo() );
        }
        else {
            reto.setTitulo( "" );
        }
        if ( dominio.getDescripcion() != null ) {
            reto.setDescripcion( dominio.getDescripcion() );
        }
        else {
            reto.setDescripcion( "" );
        }
        if ( dominio.getDificultad() != null ) {
            reto.setDificultad( dominio.getDificultad() );
        }
        else {
            reto.setDificultad( Dificultad.BASICO );
        }
        if ( dominio.getId() != null ) {
            reto.setId( dominio.getId().longValue() );
        }

        return reto;
    }

    @Override
    public List<RetoDominio> toDominio(List<Reto> entidades) {
        if ( entidades == null ) {
            return null;
        }

        List<RetoDominio> list = new ArrayList<RetoDominio>( entidades.size() );
        for ( Reto reto : entidades ) {
            list.add( toDominio( reto ) );
        }

        return list;
    }
}
