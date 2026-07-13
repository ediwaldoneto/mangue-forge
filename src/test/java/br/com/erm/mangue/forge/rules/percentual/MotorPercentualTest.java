package br.com.erm.mangue.forge.rules.percentual;

import br.com.erm.mangue.forge.domain.model.AtributosDinamicos;
import br.com.erm.mangue.forge.domain.model.Combatente;
import br.com.erm.mangue.forge.domain.model.Time;
import br.com.erm.mangue.forge.domain.resolution.FonteAleatoria;
import br.com.erm.mangue.forge.domain.resolution.ResultadoAcao;
import org.junit.jupiter.api.Test;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.*;

class MotorPercentualTest {

    private Combatente combatenteCom(String pericia, int valor) {
        return new Combatente("Atacante", new Time("herois"),
                new AtributosDinamicos(Map.of(pericia, valor)), 20);
    }

    private static class FonteFixa implements FonteAleatoria {
        private final int valorFixo;
        FonteFixa(int valorFixo) { this.valorFixo = valorFixo; }
        @Override public int rolar(int faces) { return valorFixo; }
    }

    @Test
    void sucessoQuandoRolagemMenorOuIgualAoPercentual() {
        MotorPercentual motor = new MotorPercentual(new FonteFixa(40));
        Combatente atacante = combatenteCom("mira", 60);
        Combatente alvo = combatenteCom("esquiva", 0);

        ResultadoAcao resultado = motor.resolverAcao(atacante, alvo, "mira");

        assertTrue(resultado.sucesso());
        assertEquals(21, resultado.valor());
    }

    @Test
    void falhaQuandoRolagemMaiorQueOPercentual() {
        MotorPercentual motor = new MotorPercentual(new FonteFixa(90));
        Combatente atacante = combatenteCom("mira", 60);
        Combatente alvo = combatenteCom("esquiva", 0);

        ResultadoAcao resultado = motor.resolverAcao(atacante, alvo, "mira");

        assertFalse(resultado.sucesso());
        assertEquals(0, resultado.valor());
    }
}
