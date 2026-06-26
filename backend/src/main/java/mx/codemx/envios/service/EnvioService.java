package mx.codemx.envios.service;

import mx.codemx.envios.model.Envio;
import mx.codemx.envios.repository.EnvioRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class EnvioService {

    private final EnvioRepository envioRepository;

    public EnvioService(EnvioRepository envioRepository) {
        this.envioRepository = envioRepository;
    }

    public Envio guardar(Envio envio) {
        return envioRepository.save(envio);
    }

    public Optional<Envio> buscarPorId(Long id) {
        return envioRepository.findById(id);
    }

    public List<Envio> listarPorUsuario(Long usuarioId) {
        return envioRepository.findByUsuarioId(usuarioId);
    }

    public List<Envio> listarPorReto(Long retoId) {
        return envioRepository.findByRetoId(retoId);
    }
}
