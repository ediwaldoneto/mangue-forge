package br.com.erm.mangue.forge.rules;

import br.com.erm.mangue.forge.domain.resolution.MotorDeResolucao;
import br.com.erm.mangue.forge.rules.d20.MotorD20;
import br.com.erm.mangue.forge.rules.percentual.MotorPercentual;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class MotorDeResolucaoFactoryTest {

    private final MotorDeResolucaoFactory factory = new MotorDeResolucaoFactory();

    @Test
    void criaMotorD20QuandoSolicitado() {
        MotorDeResolucao motor = factory.criar("d20", 12);
        assertTrue(motor instanceof MotorD20);
    }

    @Test
    void criaMotorPercentualQuandoSolicitado() {
        MotorDeResolucao motor = factory.criar("percentual", null);
        assertTrue(motor instanceof MotorPercentual);
    }

    @Test
    void d20SemDificuldadeLancaExcecao() {
        assertThrows(IllegalArgumentException.class, () -> factory.criar("d20", null));
    }

    @Test
    void motorDesconhecidoLancaExcecao() {
        assertThrows(IllegalArgumentException.class, () -> factory.criar("desconhecido", null));
    }
}
