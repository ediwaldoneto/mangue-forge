package br.com.erm.mangue.forge.application.usecase;

import br.com.erm.mangue.forge.domain.model.AtributosDinamicos;
import br.com.erm.mangue.forge.domain.model.Combatente;
import br.com.erm.mangue.forge.domain.model.Partida;
import br.com.erm.mangue.forge.domain.model.Time;
import br.com.erm.mangue.forge.domain.resolution.MotorDeResolucao;
import br.com.erm.mangue.forge.domain.resolution.ResultadoAcao;
import org.junit.jupiter.api.Test;
import java.util.List;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.*;

class ProcessarAtaqueUseCaseTest {

    private static class MotorFixo implements MotorDeResolucao {
        private final ResultadoAcao resultadoFixo;
        MotorFixo(ResultadoAcao resultadoFixo) { this.resultadoFixo = resultadoFixo; }
        @Override
        public ResultadoAcao resolverAcao(Combatente origem, Combatente alvo, String nomeDaAcao) {
            return resultadoFixo;
        }
    }

    private Combatente combatenteComAgilidade(String nome, int agilidade, int hp) {
        return new Combatente(nome, new Time("herois"),
                new AtributosDinamicos(Map.of("agilidade", agilidade)), hp);
    }

    @Test
    void ataqueComSucessoAplicaDanoEAvancaTurno() {
        Combatente atacante = combatenteComAgilidade("Atacante", 9, 10);
        Combatente alvo = combatenteComAgilidade("Alvo", 3, 10);
        Partida partida = new IniciarPartidaUseCase().executar(List.of(atacante, alvo), "agilidade");

        ProcessarAtaqueUseCase useCase = new ProcessarAtaqueUseCase(
                new MotorFixo(new ResultadoAcao(true, 6, "acertou")));

        ResultadoAcao resultado = useCase.executar(partida, alvo, "forca");

        assertTrue(resultado.sucesso());
        assertEquals(4, alvo.getHp().getAtual());
        assertEquals(alvo.getId(), partida.combatenteDaVez().getId());
    }

    @Test
    void ataqueSemSucessoNaoAplicaDanoMasAvancaTurno() {
        Combatente atacante = combatenteComAgilidade("Atacante", 9, 10);
        Combatente alvo = combatenteComAgilidade("Alvo", 3, 10);
        Partida partida = new IniciarPartidaUseCase().executar(List.of(atacante, alvo), "agilidade");

        ProcessarAtaqueUseCase useCase = new ProcessarAtaqueUseCase(
                new MotorFixo(new ResultadoAcao(false, 0, "errou")));

        ResultadoAcao resultado = useCase.executar(partida, alvo, "forca");

        assertFalse(resultado.sucesso());
        assertEquals(10, alvo.getHp().getAtual());
        assertEquals(alvo.getId(), partida.combatenteDaVez().getId());
    }
}
