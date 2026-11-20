package mx.codemx.gateway;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import mx.codemx.modules.auth.FirebaseLoginService;
import mx.codemx.modules.auth.LoginResponse;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class FirebaseAuthController {
    private final FirebaseLoginService service;

    public FirebaseAuthController(FirebaseLoginService service) { this.service = service; }

    @Operation(summary = "Intercambiar un ID token de Firebase por una sesión de CodeMx")
    @SecurityRequirements
    @PostMapping("/auth/firebase")
    public LoginResponse login(@Valid @RequestBody FirebaseLoginRequest request) {
        return service.login(request.idToken());
    }

    public record FirebaseLoginRequest(@NotBlank @Size(max = 16384) String idToken) { }
}
