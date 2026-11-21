package mx.codemx.gateway;

import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import mx.codemx.modules.usuarios.UsuariosApi;
import mx.codemx.modules.usuarios.domain.UsuarioDominio;
import mx.codemx.modules.usuarios.mapper.UsuarioMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Gateway / API REST → módulo Usuarios.
 *
 * <p>Expone {@link UsuarioDominio} (POJO sin JPA), nunca la entidad: el mapeador de
 * MapStruct traduce en la frontera, así la estructura de la tabla no se filtra al contrato.
 */
@RestController
@RequestMapping("/api/usuarios")
@Tag(name = "Usuarios")
public class UsuariosController {

    /** Credenciales para iniciar sesión. */
    public record CredencialesLogin(String email, String passwordHash) {
    }

    private final UsuariosApi usuarios;
    private final UsuarioMapper mapper;

    public UsuariosController(UsuariosApi usuarios, UsuarioMapper mapper) {
        this.usuarios = usuarios;
        this.mapper = mapper;
    }

    @GetMapping
    public List<UsuarioDominio> listar() {
        return mapper.toDominio(usuarios.listarTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<UsuarioDominio> obtener(@PathVariable long id) {
        return usuarios.buscarPorId(id)
                .map(mapper::toDominio)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public UsuarioDominio crear(@RequestBody UsuarioDominio usuario) {
        return mapper.toDominio(usuarios.guardar(mapper.toEntity(usuario)));
    }

    /** Inicia sesión validando correo + contraseña. */
    @PostMapping("/login")
    public ResponseEntity<UsuarioDominio> login(@RequestBody CredencialesLogin credenciales) {
        return usuarios.autenticar(credenciales.email(), credenciales.passwordHash())
                .map(mapper::toDominio)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(401).build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable long id) {
        usuarios.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
