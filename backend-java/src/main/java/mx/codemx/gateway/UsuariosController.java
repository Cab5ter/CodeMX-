package mx.codemx.gateway;

import io.swagger.v3.oas.annotations.tags.Tag;
import java.net.URI;
import java.util.List;
import mx.codemx.modules.usuarios.UsuariosApi;
import mx.codemx.modules.usuarios.domain.UsuarioDominio;
import mx.codemx.modules.usuarios.mapper.UsuarioMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/usuarios")
@Tag(name = "Usuarios")
public class UsuariosController {

    public record CredencialesLogin(String email, String passwordHash) {
    }

    private final UsuariosApi usuarios;
    private final UsuarioMapper mapper;

    public UsuariosController(UsuariosApi usuarios, UsuarioMapper mapper) {
        this.usuarios = usuarios;
        this.mapper = mapper;
    }

    @GetMapping
    public ResponseEntity<List<UsuarioDominio>> listar(@RequestParam(required = false) String email) {
        if (email == null) {
            return ResponseEntity.ok(mapper.toDominio(usuarios.listarTodos()));
        }
        return ResponseEntity.ok(usuarios.buscarPorEmail(email)
                .map(mapper::toDominio)
                .map(List::of)
                .orElseGet(List::of));
    }

    @GetMapping("/{id}")
    public ResponseEntity<UsuarioDominio> obtener(@PathVariable long id) {
        return usuarios.buscarPorId(id)
                .map(mapper::toDominio)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<UsuarioDominio> crear(@RequestBody UsuarioDominio usuario) {
        UsuarioDominio creado = mapper.toDominio(usuarios.guardar(mapper.toEntity(usuario)));
        return ResponseEntity.created(URI.create("/api/usuarios/" + creado.getId())).body(creado);
    }

    @PostMapping("/login")
    public ResponseEntity<UsuarioDominio> login(@RequestBody CredencialesLogin credenciales) {
        return usuarios.autenticar(credenciales.email(), credenciales.passwordHash())
                .map(mapper::toDominio)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.UNAUTHORIZED).build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable long id) {
        if (usuarios.buscarPorId(id).isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        usuarios.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
