package mx.codemx.modules.ranking;

import mx.codemx.modules.envios.EnvioAceptadoEvent;
import mx.codemx.modules.envios.EnvioObserver;
import org.springframework.stereotype.Component;

@Component
public class RankingEnvioObserver implements EnvioObserver {

    private final RankingApi ranking;

    public RankingEnvioObserver(RankingApi ranking) {
        this.ranking = ranking;
    }

    @Override
    public void onEnvioAceptado(EnvioAceptadoEvent evento) {
        ranking.registrarAcierto(evento.usuarioId(), evento.retoId());
    }
}
