namespace CodeMX.Api.Modules.Usuarios;

/// <summary>Se intentó registrar un correo que ya tiene cuenta.</summary>
public class EmailYaRegistradoException : Exception
{
    public EmailYaRegistradoException(string email)
        : base($"Ya existe una cuenta con el correo '{email}'.") { }
}

/// <summary>Los datos de registro no cumplen las reglas mínimas.</summary>
public class RegistroInvalidoException : Exception
{
    public RegistroInvalidoException(string mensaje) : base(mensaje) { }
}
