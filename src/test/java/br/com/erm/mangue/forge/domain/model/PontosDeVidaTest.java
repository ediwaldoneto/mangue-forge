package br.com.erm.mangue.forge.domain.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class PontosDeVidaTest {

    @Test
    void criaComValoresValidos() {
        PontosDeVida hp = new PontosDeVida(10, 10);
        assertEquals(10, hp.getAtual());
        assertEquals(10, hp.getMaximo());
    }

    @Test
    void maximoMenorOuIgualAZeroLancaExcecao() {
        assertThrows(IllegalArgumentException.class, () -> new PontosDeVida(5, 0));
    }

    @Test
    void atualAcimaDoMaximoEClampadoParaOMaximo() {
        PontosDeVida hp = new PontosDeVida(999, 10);
        assertEquals(10, hp.getAtual());
    }

    @Test
    void atualNegativoEClampadoParaZero() {
        PontosDeVida hp = new PontosDeVida(-5, 10);
        assertEquals(0, hp.getAtual());
    }

    @Test
    void receberDanoReduzAtualSemPassarDeZero() {
        PontosDeVida hp = new PontosDeVida(10, 10);
        PontosDeVida depois = hp.receberDano(4);
        assertEquals(6, depois.getAtual());

        PontosDeVida quaseMorto = new PontosDeVida(3, 10);
        assertEquals(0, quaseMorto.receberDano(50).getAtual());
    }

    @Test
    void receberDanoNaoAlteraInstanciaOriginal() {
        PontosDeVida hp = new PontosDeVida(10, 10);
        hp.receberDano(4);
        assertEquals(10, hp.getAtual());
    }

    @Test
    void receberDanoNegativoLancaExcecao() {
        PontosDeVida hp = new PontosDeVida(10, 10);
        assertThrows(IllegalArgumentException.class, () -> hp.receberDano(-1));
    }

    @Test
    void curarAumentaAtualSemPassarDoMaximo() {
        PontosDeVida hp = new PontosDeVida(2, 10);
        assertEquals(7, hp.curar(5).getAtual());
        assertEquals(10, hp.curar(999).getAtual());
    }

    @Test
    void estaMortoQuandoAtualEZero() {
        PontosDeVida hp = new PontosDeVida(0, 10);
        assertTrue(hp.estaMorto());
        assertFalse(new PontosDeVida(1, 10).estaMorto());
    }
}
