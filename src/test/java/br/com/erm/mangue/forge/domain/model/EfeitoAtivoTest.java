package br.com.erm.mangue.forge.domain.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class EfeitoAtivoTest {

    @Test
    void criaComValoresValidos() {
        EfeitoAtivo veneno = new EfeitoAtivo("Veneno", TipoEfeito.DANO_POR_TURNO, 5, 3);
        assertEquals("Veneno", veneno.getNome());
        assertEquals(TipoEfeito.DANO_POR_TURNO, veneno.getTipo());
        assertEquals(5, veneno.getValorPorTurno());
        assertEquals(3, veneno.getDuracaoRestante());
    }

    @Test
    void valorPorTurnoNegativoLancaExcecao() {
        assertThrows(IllegalArgumentException.class,
                () -> new EfeitoAtivo("Veneno", TipoEfeito.DANO_POR_TURNO, -1, 3));
    }

    @Test
    void duracaoRestanteNegativaLancaExcecao() {
        assertThrows(IllegalArgumentException.class,
                () -> new EfeitoAtivo("Veneno", TipoEfeito.DANO_POR_TURNO, 5, -1));
    }

    @Test
    void duracaoRestanteZeroEValida() {
        EfeitoAtivo efeito = new EfeitoAtivo("Veneno", TipoEfeito.DANO_POR_TURNO, 5, 0);
        assertEquals(0, efeito.getDuracaoRestante());
    }

    @Test
    void decrementarDuracaoReduzEmUmSemAlterarOOriginal() {
        EfeitoAtivo veneno = new EfeitoAtivo("Veneno", TipoEfeito.DANO_POR_TURNO, 5, 3);
        EfeitoAtivo depois = veneno.decrementarDuracao();

        assertEquals(2, depois.getDuracaoRestante());
        assertEquals(3, veneno.getDuracaoRestante());
    }

    @Test
    void expirouRefleteDuracaoRestanteMenorOuIgualAZero() {
        EfeitoAtivo quaseExpirando = new EfeitoAtivo("Veneno", TipoEfeito.DANO_POR_TURNO, 5, 1);
        assertFalse(quaseExpirando.expirou());
        assertTrue(quaseExpirando.decrementarDuracao().expirou());
    }
}
