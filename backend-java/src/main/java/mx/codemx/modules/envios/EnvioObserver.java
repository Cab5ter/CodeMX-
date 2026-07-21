package mx.codemx.modules.envios;

/**
 * Patrón <b>Observer</b> (GoF). El módulo Envíos (sujeto) notifica a los observadores
 * registrados cuando un envío es ACEPTADO por primera vez, sin conocer quién reacciona.
 * El módulo Ranking implementa un observador; en el futuro podrían sumarse otros
 * (logros, notificaciones, etc.) sin tocar Envíos.
 */
public interface EnvioObserver {

    void onEnvioAceptado(EnvioAceptadoEvent evento);
}
