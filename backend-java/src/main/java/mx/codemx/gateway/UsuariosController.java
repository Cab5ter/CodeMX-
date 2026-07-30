package mx.codemx.gateway;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
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
@Tag(name = "Usuarios", description = "Alta, consulta y autenticación de los estudiantes de la plataforma.")
public class UsuariosController {

    public record CredencialesLogin(String email, String passwordHash) {
    }

    private final UsuariosApi usuarios;
    private final UsuarioMapper mapper;

    public UsuariosController(UsuariosApi usuarios, UsuarioMapper mapper) {
        this.usuarios = usuarios;
        this.mapper = mapper;
    }

    @Operation(
            summary = "Listar usuarios registrados",
            description = """
                    Devuelve todos los usuarios de la plataforma. Si se envía el parámetro `email`,
                    filtra por ese correo exacto y devuelve una lista con un único elemento, o una
                    lista vacía si nadie lo tiene registrado. Se usa para verificar si un correo ya
                    está ocupado antes de registrar una cuenta nueva.""")
    @ApiResponse(responseCode = "200", description = "Lista de usuarios (posiblemente vacía)")
    @GetMapping
    public ResponseEntity<List<UsuarioDominio>> listar(
            @Parameter(description = "Correo exacto por el cual filtrar. Si se omite, devuelve todos.")
            @RequestParam(required = false) String email) {
        if (email == null) {
            return ResponseEntity.ok(mapper.toDominio(usuarios.listarTodos()));
        }
        return ResponseEntity.ok(usuarios.buscarPorEmail(email)
                .map(mapper::toDominio)
                .map(List::of)
                .orElseGet(List::of));
    }

    @Operation(
            summary = "Obtener un usuario por su identificador",
            description = "Devuelve el perfil de un usuario concreto: nombre, correo y fecha de alta.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Usuario encontrado"),
            @ApiResponse(responseCode = "404", description = "No existe un usuario con ese identificador")
    })
    @GetMapping("/{id}")
    public ResponseEntity<UsuarioDominio> obtener(
            @Parameter(description = "Identificador del usuario") @PathVariable long id) {
        return usuarios.buscarPorId(id)
                .map(mapper::toDominio)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Operation(
            summary = "Registrar un usuario nuevo",
            description = """
                    Crea la cuenta del estudiante y devuelve el recurso creado junto con la cabecera
                    `Location` que apunta a él. El `id` lo asigna siempre la base de datos: si el
                    cuerpo trae uno, se ignora.

                    **Deuda técnica DT-01:** la contraseña se recibe y se guarda en texto plano en el
                    campo `passwordHash`, que además viaja de vuelta en la respuesta. Ver el README.""")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Usuario creado"),
            @ApiResponse(responseCode = "409", description = "El correo ya está registrado")
    })
    @PostMapping
    public ResponseEntity<UsuarioDominio> crear(@Valid @RequestBody UsuarioDominio usuario) {
        usuario.setId(null);
        UsuarioDominio creado = mapper.toDominio(usuarios.guardar(mapper.toEntity(usuario)));
        return ResponseEntity.created(URI.create("/api/usuarios/" + creado.getId())).body(creado);
    }

    @Operation(
            summary = "Iniciar sesión",
            description = """
                    Busca al usuario por correo y compara la contraseña recibida con la almacenada.
                    Devuelve el usuario cuando coinciden y `401` cuando no. No emite ningún token ni
                    cookie de sesión: el frontend guarda el objeto del usuario y las demás operaciones
                    confían en el `usuarioId` que envía el cliente (deuda técnica DT-01).""")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Credenciales válidas; devuelve el usuario"),
            @ApiResponse(responseCode = "401", description = "Correo inexistente o contraseña incorrecta")
    })
    @PostMapping("/login")
    public ResponseEntity<UsuarioDominio> login(@RequestBody CredencialesLogin credenciales) {
        return usuarios.autenticar(credenciales.email(), credenciales.passwordHash())
                .map(mapper::toDominio)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.UNAUTHORIZED).build());
    }

    @Operation(
            summary = "Eliminar un usuario",
            description = "Borra la cuenta de forma permanente. La operación no pide confirmación.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Usuario eliminado"),
            @ApiResponse(responseCode = "404", description = "No existe un usuario con ese identificador")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(
            @Parameter(description = "Identificador del usuario a eliminar") @PathVariable long id) {
        if (usuarios.buscarPorId(id).isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        usuarios.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
