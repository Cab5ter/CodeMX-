# CodeMX

Plataforma de retos de programacion en espanol para estudiantes universitarios en Mexico.
Este repositorio contiene las decisiones de arquitectura (ADRs) y la logica de dominio del
proyecto, con pruebas unitarias y un pipeline de integracion continua.

## Estructura

- src/CodeMX.Domain            Logica de dominio (Challenge, Submission, Leaderboard)
- tests/CodeMX.Domain.Tests    Pruebas unitarias con xUnit (Arrange-Act-Assert)
- .github/workflows/ci.yml     Pipeline de CI: compila y corre las pruebas en cada push/PR
- ADR-*.md                     Registros de decisiones de arquitectura

## Como ejecutar las pruebas

Requiere el SDK de .NET 8. En la raiz del repositorio:

    dotnet restore CodeMX.sln
    dotnet build CodeMX.sln --configuration Release
    dotnet test CodeMX.sln --configuration Release

## Integracion continua

Cada push y cada Pull Request dispara el workflow CI (GitHub Actions), que compila la
solucion y ejecuta la suite de pruebas xUnit. El check verde o rojo aparece en la pestana
Actions y en el Pull Request correspondiente. Ver el ADR-05 para el detalle de que clases
se prueban y por que.
