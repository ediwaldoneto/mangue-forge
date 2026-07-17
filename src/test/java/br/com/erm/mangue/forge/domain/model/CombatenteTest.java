package br.com.erm.mangue.forge.domain.model;

import org.junit.jupiter.api.Test;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.*;

class CombatenteTest {

    private Combatente novoCombatente(int hpMaximo) {
        return new Combatente("Guerreiro", new Time("herois"),
                new AtributosDinamicos(Map.of("forca", 5, "agilidade", 8)), hpMaximo);
    }

    @Test
    void novoCombatenteComecaVivoComHpCheio() {
        Combatente c = novoCombatente(20);
        assertEquals(20, c.getHp().getAtual());
        assertTrue(c.estaVivo());
    }

    @Test
    void idsSaoUnicosEntreCombatentesDiferentes() {
        Combatente a = novoCombatente(20);
        Combatente b = novoCombatente(20);
        assertNotEquals(a.getId(), b.getId());
    }

    @Test
    void aplicarDanoReduzHp() {
        Combatente c = novoCombatente(20);
        c.aplicarDano(6);
        assertEquals(14, c.getHp().getAtual());
    }

    @Test
    void aplicarDanoFatalDeixaCombatenteMorto() {
        Combatente c = novoCombatente(10);
        c.aplicarDano(999);
        assertFalse(c.estaVivo());
    }

    @Test
    void receberCuraAumentaHp() {
        Combatente c = novoCombatente(20);
        c.aplicarDano(15);
        c.receberCura(5);
        assertEquals(10, c.getHp().getAtual());
    }

    @Test
    void atributosSaoAcessiveisPorChave() {
        Combatente c = novoCombatente(20);
        assertEquals(8, c.getAtributos().obterValor("agilidade"));
    }

    @Test
    void aplicarEfeitoAdicionaAListaDeEfeitosAtivos() {
        Combatente c = novoCombatente(20);
        EfeitoAtivo veneno = new EfeitoAtivo("Veneno", TipoEfeito.DANO_POR_TURNO, 5, 2);

        c.aplicarEfeito(veneno);

        assertEquals(1, c.getEfeitosAtivos().size());
    }

    @Test
    void processarInicioDoTurnoAplicaDanoPorTurno() {
        Combatente c = novoCombatente(20);
        c.aplicarEfeito(new EfeitoAtivo("Veneno", TipoEfeito.DANO_POR_TURNO, 5, 2));

        c.processarInicioDoTurno();

        assertEquals(15, c.getHp().getAtual());
    }

    @Test
    void processarInicioDoTurnoAplicaCuraPorTurno() {
        Combatente c = novoCombatente(20);
        c.aplicarDano(10);
        c.aplicarEfeito(new EfeitoAtivo("Regeneracao", TipoEfeito.CURA_POR_TURNO, 4, 2));

        c.processarInicioDoTurno();

        assertEquals(14, c.getHp().getAtual());
    }

    @Test
    void processarInicioDoTurnoDecrementaDuracaoDoEfeito() {
        Combatente c = novoCombatente(20);
        c.aplicarEfeito(new EfeitoAtivo("Veneno", TipoEfeito.DANO_POR_TURNO, 5, 2));

        c.processarInicioDoTurno();

        assertEquals(1, c.getEfeitosAtivos().get(0).getDuracaoRestante());
    }

    @Test
    void efeitoEhRemovidoDaListaQuandoExpira() {
        Combatente c = novoCombatente(20);
        c.aplicarEfeito(new EfeitoAtivo("Veneno", TipoEfeito.DANO_POR_TURNO, 5, 1));

        c.processarInicioDoTurno();

        assertTrue(c.getEfeitosAtivos().isEmpty());
    }

    @Test
    void multiplosEfeitosSaoTodosProcessadosNoMesmoTurno() {
        Combatente c = novoCombatente(20);
        c.aplicarEfeito(new EfeitoAtivo("Veneno", TipoEfeito.DANO_POR_TURNO, 5, 2));
        c.aplicarEfeito(new EfeitoAtivo("Regeneracao", TipoEfeito.CURA_POR_TURNO, 2, 2));

        c.processarInicioDoTurno();

        assertEquals(17, c.getHp().getAtual());
    }
}
