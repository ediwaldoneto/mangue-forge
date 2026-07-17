package br.com.erm.mangue.forge.infrastructure.persistence;

import br.com.erm.mangue.forge.application.exception.PartidaNaoEncontradaException;
import br.com.erm.mangue.forge.domain.model.AtributosDinamicos;
import br.com.erm.mangue.forge.domain.model.Combatente;
import br.com.erm.mangue.forge.domain.model.Partida;
import br.com.erm.mangue.forge.domain.model.Time;
import br.com.erm.mangue.forge.domain.resolution.MotorDeResolucao;
import br.com.erm.mangue.forge.domain.resolution.ResultadoAcao;
import org.junit.jupiter.api.Test;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.*;

class PartidaEmMemoriaRepositoryTest {

    private static class MotorFixo implements MotorDeResolucao {
        @Override
        public ResultadoAcao resolverAcao(Combatente origem, Combatente alvo, String nomeDaAcao) {
            return new ResultadoAcao(true, 1, "fixo");
        }
    }

    @Test
    void salvarEBuscarRetornaAMesmaPartidaAtiva() {
        Combatente c = new Combatente("A", new Time("herois"), new AtributosDinamicos(Map.of("agilidade", 5)), 10);
        Partida partida = new Partida(List.of(c), "agilidade");
        PartidaAtiva ativa = new PartidaAtiva(partida, new MotorFixo());

        PartidaEmMemoriaRepository repositorio = new PartidaEmMemoriaRepository();
        repositorio.salvar(ativa);

        PartidaAtiva encontrada = repositorio.buscarPorId(partida.getId());
        assertEquals(partida.getId(), encontrada.partida().getId());
    }

    @Test
    void buscarPorIdInexistenteLancaExcecao() {
        PartidaEmMemoriaRepository repositorio = new PartidaEmMemoriaRepository();
        assertThrows(PartidaNaoEncontradaException.class, () -> repositorio.buscarPorId(UUID.randomUUID()));
    }
}
