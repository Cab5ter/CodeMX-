using CodeMX.Api.Modules.Cursos;
using CodeMX.Api.Modules.Duelos;
using CodeMX.Api.Modules.Envios;
using CodeMX.Api.Modules.Evaluacion;
using CodeMX.Api.Modules.Ranking;
using CodeMX.Api.Modules.Retos;
using CodeMX.Api.Modules.Usuarios;
using CodeMX.Api.Persistence;
using CodeMX.Api.RealTime;
using Microsoft.EntityFrameworkCore;
using System.Text.Json.Serialization;

var builder = WebApplication.CreateBuilder(args);

// --- Web API + Swagger/OpenAPI (Swashbuckle) ---
// Los enums (Dificultad, Veredicto) se serializan como texto, igual que la versión Spring Boot.
builder.Services.AddControllers().AddJsonOptions(o =>
    o.JsonSerializerOptions.Converters.Add(new JsonStringEnumConverter()));
builder.Services.AddEndpointsApiExplorer();
builder.Services.AddSwaggerGen(options =>
{
    options.SwaggerDoc("v1", new()
    {
        Title = "CodeMX API",
        Version = "v1",
        Description = "API REST del monolito modular de CodeMX (ASP.NET Core). Contrato documentado con Swagger/OpenAPI — ADR-04."
    });
});

// --- Persistencia: Entity Framework Core + PostgreSQL (Npgsql) ---
builder.Services.AddDbContext<CodeMxDbContext>(opt =>
    opt.UseNpgsql(builder.Configuration.GetConnectionString("Postgres")));

// --- Repositorios por módulo ---
builder.Services.AddScoped<UsuarioRepository>();
builder.Services.AddScoped<RetoRepository>();
builder.Services.AddScoped<EnvioRepository>();
builder.Services.AddScoped<RankingRepository>();
builder.Services.AddScoped<CursoRepository>();

// --- APIs públicas de cada módulo (interfaces entre módulos) ---
builder.Services.AddScoped<IUsuariosApi, UsuarioService>();
builder.Services.AddScoped<IRetosApi, RetoService>();
builder.Services.AddScoped<IEnviosApi, EnvioService>();
builder.Services.AddScoped<IRankingApi, RankingService>();
builder.Services.AddScoped<IEvaluacionApi, EvaluacionService>();
builder.Services.AddScoped<ICursosApi, CursoService>();

// --- Patrón Strategy + Factory Method para el módulo Evaluación ---
builder.Services.AddHttpClient<EvaluacionRemotaStrategy>();
builder.Services.AddScoped<EvaluacionLocalStrategy>();
builder.Services.AddScoped<IEvaluadorStrategyFactory, EvaluadorStrategyFactory>();

// --- Patrón Observer: Ranking se suscribe a los envíos aceptados ---
builder.Services.AddScoped<IEnvioObserver, RankingEnvioObserver>();

// --- Módulo Duelos (1 vs 1) + tiempo real con SignalR ---
builder.Services.AddScoped<DueloRepository>();
builder.Services.AddScoped<IDuelosApi, DueloService>();
builder.Services.AddScoped<ClaudeGeneradorProblemas>();
builder.Services.AddScoped<RetoSembradoGenerador>();
builder.Services.AddScoped<IGeneradorProblemas, GeneradorProblemas>();
builder.Services.AddSingleton<MatchmakingService>();   // cola y duelos activos en memoria
builder.Services.AddSignalR();

// --- CORS para el frontend React (Vite) ---
builder.Services.AddCors(o => o.AddDefaultPolicy(p =>
    p.AllowAnyOrigin().AllowAnyHeader().AllowAnyMethod()));

var app = builder.Build();

// Crea la base, los esquemas por módulo y las tablas; siembra los retos de ejemplo.
using (var scope = app.Services.CreateScope())
{
    var db = scope.ServiceProvider.GetRequiredService<CodeMxDbContext>();
    db.Database.EnsureCreated();
    DataSeeder.Seed(db);
}

// Swagger disponible siempre (la documentación del contrato es parte del ADR-04).
app.UseSwagger();
app.UseSwaggerUI(o =>
{
    o.SwaggerEndpoint("/swagger/v1/swagger.json", "CodeMX API v1");
    o.RoutePrefix = "swagger";
});

app.UseCors();
app.MapControllers();
app.MapHub<DueloHub>("/api/hub/duelos");   // canal de tiempo real del modo 1 vs 1

app.Run();
