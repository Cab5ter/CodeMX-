# Imagen única de CodeMX: la API de ASP.NET Core sirve también el frontend de React.
# Un solo servicio que desplegar, un solo origen y por tanto sin CORS (ADR-08).

# --- Etapa 1: build del frontend (React + Vite) ---
FROM node:20-alpine AS frontend
WORKDIR /frontend
# Se copian primero los manifiestos para que la capa de dependencias se cachee.
COPY frontend/package.json frontend/package-lock.json ./
RUN npm ci
COPY frontend/ ./
RUN npm run build

# --- Etapa 2: build y publicación del backend (.NET 10) ---
FROM mcr.microsoft.com/dotnet/sdk:10.0 AS backend
WORKDIR /src
COPY backend/CodeMX.Api.csproj backend/
RUN dotnet restore backend/CodeMX.Api.csproj
COPY backend/ backend/
RUN dotnet publish backend/CodeMX.Api.csproj -c Release -o /app/publish /p:UseAppHost=false

# --- Etapa 3: imagen de ejecución ---
FROM mcr.microsoft.com/dotnet/aspnet:10.0 AS runtime
WORKDIR /app
COPY --from=backend /app/publish ./
# El build de Vite se sirve como contenido estático desde la propia API.
COPY --from=frontend /frontend/dist ./wwwroot

# Render sobrescribe PORT; 8080 es el valor por defecto para correr la imagen en local.
ENV ASPNETCORE_ENVIRONMENT=Production
ENV PORT=8080
EXPOSE 8080

# Se ejecuta como usuario sin privilegios: si alguien escapa del proceso, no es root.
USER $APP_UID

ENTRYPOINT ["dotnet", "CodeMX.Api.dll"]
