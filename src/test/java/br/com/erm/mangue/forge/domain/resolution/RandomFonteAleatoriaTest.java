package br.com.erm.mangue.forge.domain.resolution;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class RandomFonteAleatoriaTest {

    @Test
    void rolagemFicaSempreDentroDoIntervaloValido() {
        RandomFonteAleatoria fonte = new RandomFonteAleatoria();

        for (int i = 0; i < 1000; i++) {
            int rolagem = fonte.rolar(20);
            assertTrue(rolagem >= 1 && rolagem <= 20);
        }
    }

    @Test
    void dadoDeUmaFaceSempreRetornaUm() {
        RandomFonteAleatoria fonte = new RandomFonteAleatoria();
        assertEquals(1, fonte.rolar(1));
    }
}
