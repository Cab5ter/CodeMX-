package mx.codemx.gateway;

import mx.codemx.usuarios.api.UsuariosApi;
import mx.codemx.usuarios.model.Usuario;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Gateway interno → módulo Usuarios.
 * Recibe las peticiones REST/JSON del frontend y las delega a la interfaz del módulo.
 */
@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

    private final UsuariosApi usuarios;

    public UsuarioController(UsuariosApi usuarios) {
        this.usuarios = usuarios;
    }

    @GetMapping
    public ResponseEntity<List<Usuario>> listar() {
        return ResponseEntity.ok(usuarios.listarTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Usuario> obtener(@PathVariable Long id) {
        return usuarios.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Usuario> crear(@RequestBody Usuario usuario) {
        return ResponseEntity.ok(usuarios.guardar(usuario));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        usuarios.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
