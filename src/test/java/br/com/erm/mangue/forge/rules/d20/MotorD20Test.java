package br.com.erm.mangue.forge.rules.d20;

import br.com.erm.mangue.forge.domain.model.AtributosDinamicos;
import br.com.erm.mangue.forge.domain.model.Combatente;
import br.com.erm.mangue.forge.domain.model.Time;
import br.com.erm.mangue.forge.domain.resolution.FonteAleatoria;
import br.com.erm.mangue.forge.domain.resolution.ResultadoAcao;
import org.junit.jupiter.api.Test;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.*;

class MotorD20Test {

    private Combatente combatenteCom(String atributo, int valor) {
        return new Combatente("Atacante", new Time("herois"),
                new AtributosDinamicos(Map.of(atributo, valor)), 20);
    }

    private static class FonteFixa implements FonteAleatoria {
        private final int valorFixo;
        FonteFixa(int valorFixo) { this.valorFixo = valorFixo; }
        @Override public int rolar(int faces) { return valorFixo; }
    }

    @Test
    void sucessoQuandoTotalAtingeADificuldade() {
        MotorD20 motor = new MotorD20(new FonteFixa(15), 15);
        Combatente atacante = combatenteCom("forca", 5);
        Combatente alvo = combatenteCom("defesa", 0);

        ResultadoAcao resultado = motor.resolverAcao(atacante, alvo, "forca");

        assertTrue(resultado.sucesso());
        assertEquals(5, resultado.valor());
    }

    @Test
    void falhaQuandoTotalNaoAtingeADificuldade() {
        MotorD20 motor = new MotorD20(new FonteFixa(2), 15);
        Combatente atacante = combatenteCom("forca", 1);
        Combatente alvo = combatenteCom("defesa", 0);

        ResultadoAcao resultado = motor.resolverAcao(atacante, alvo, "forca");

        assertFalse(resultado.sucesso());
        assertEquals(0, resultado.valor());
    }
}
