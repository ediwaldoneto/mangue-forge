package br.com.erm.mangue.forge.infrastructure.persistence;

import br.com.erm.mangue.forge.application.exception.PartidaNaoEncontradaException;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Armazena partidas ativas em memória, pelo tempo de vida do processo.
 *
 * <p>{@link ConcurrentHashMap} garante que criar/consultar partidas diferentes
 * simultaneamente é seguro. Mutações concorrentes na mesma partida não são
 * protegidas por lock — cenário fora de escopo, coerente com o isolamento
 * (não concorrência interna) já assumido para {@link br.com.erm.mangue.forge.domain.model.Partida}.</p>
 */
@Component
public final class PartidaEmMemoriaRepository {

    private final Map<UUID, PartidaAtiva> partidas = new ConcurrentHashMap<>();

    public void salvar(PartidaAtiva partidaAtiva) {
        partidas.put(partidaAtiva.partida().getId(), partidaAtiva);
    }

    /** @throws PartidaNaoEncontradaException se {@code id} não corresponder a nenhuma partida ativa */
    public PartidaAtiva buscarPorId(UUID id) {
        PartidaAtiva encontrada = partidas.get(id);
        if (encontrada == null) {
            throw new PartidaNaoEncontradaException("Partida não encontrada: " + id);
        }
        return encontrada;
    }
}
