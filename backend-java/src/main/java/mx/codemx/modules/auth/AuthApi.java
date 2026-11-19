package mx.codemx.modules.auth;

public interface AuthApi {

    LoginResponse iniciarSesion(LoginRequest credenciales);
}
