using CodeMX.Api.Modules.Cursos;
using CodeMX.Api.Modules.Envios;
using CodeMX.Api.Modules.Ranking;
using CodeMX.Api.Modules.Retos;
using CodeMX.Api.Modules.Usuarios;
using Microsoft.EntityFrameworkCore;

namespace CodeMX.Api.Persistence;

/// <summary>
/// Contexto de Entity Framework Core. PostgreSQL es la única fuente de verdad,
/// con un esquema lógico por módulo (usuarios, retos, envios, ranking), igual que
/// en la implementación Spring Boot del ADR-03.
/// </summary>
public class CodeMxDbContext : DbContext
{
    public CodeMxDbContext(DbContextOptions<CodeMxDbContext> options) : base(options) { }

    public DbSet<Usuario> Usuarios => Set<Usuario>();
    public DbSet<Reto> Retos => Set<Reto>();
    public DbSet<CasoPrueba> CasosPrueba => Set<CasoPrueba>();
    public DbSet<Envio> Envios => Set<Envio>();
    public DbSet<EntradaRanking> Ranking => Set<EntradaRanking>();
    public DbSet<Modulo> Modulos => Set<Modulo>();
    public DbSet<Leccion> Lecciones => Set<Leccion>();
    public DbSet<ProgresoLeccion> ProgresoLecciones => Set<ProgresoLeccion>();
    public DbSet<PreguntaExamen> PreguntasExamen => Set<PreguntaExamen>();

    protected override void OnModelCreating(ModelBuilder model)
    {
        // --- esquema usuarios ---
        model.Entity<Usuario>(e =>
        {
            e.ToTable("usuarios", schema: "usuarios");
            e.HasKey(x => x.Id);
            e.HasIndex(x => x.Email).IsUnique();
        });

        // --- esquema retos ---
        model.Entity<Reto>(e =>
        {
            e.ToTable("retos", schema: "retos");
            e.HasKey(x => x.Id);
            e.Property(x => x.Dificultad).HasConversion<string>();
        });
        model.Entity<CasoPrueba>(e =>
        {
            e.ToTable("casos_prueba", schema: "retos");
            e.HasKey(x => x.Id);
        });

        // --- esquema envios ---
        model.Entity<Envio>(e =>
        {
            e.ToTable("envios", schema: "envios");
            e.HasKey(x => x.Id);
            e.Property(x => x.Veredicto).HasConversion<string>();
        });

        // --- esquema ranking ---
        model.Entity<EntradaRanking>(e =>
        {
            e.ToTable("ranking", schema: "ranking");
            e.HasKey(x => x.Id);
            e.HasIndex(x => x.UsuarioId).IsUnique();
        });

        // --- esquema cursos ---
        model.Entity<Modulo>(e =>
        {
            e.ToTable("modulos", schema: "cursos");
            e.HasKey(x => x.Id);
        });
        model.Entity<Leccion>(e =>
        {
            e.ToTable("lecciones", schema: "cursos");
            e.HasKey(x => x.Id);
            e.Property(x => x.Tipo).HasConversion<string>();
        });
        model.Entity<ProgresoLeccion>(e =>
        {
            e.ToTable("progreso_lecciones", schema: "cursos");
            e.HasKey(x => x.Id);
            e.HasIndex(x => new { x.UsuarioId, x.LeccionId }).IsUnique();
        });
        model.Entity<PreguntaExamen>(e =>
        {
            e.ToTable("preguntas_examen", schema: "cursos");
            e.HasKey(x => x.Id);
        });
    }
}
