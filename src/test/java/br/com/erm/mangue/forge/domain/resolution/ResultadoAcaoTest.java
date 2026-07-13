package br.com.erm.mangue.forge.domain.resolution;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ResultadoAcaoTest {

    @Test
    void expoeSeusCampos() {
        ResultadoAcao resultado = new ResultadoAcao(true, 7, "acerto critico");
        assertTrue(resultado.sucesso());
        assertEquals(7, resultado.valor());
        assertEquals("acerto critico", resultado.descricao());
    }

    @Test
    void doisResultadosComMesmosCamposSaoIguais() {
        ResultadoAcao a = new ResultadoAcao(false, 0, "errou");
        ResultadoAcao b = new ResultadoAcao(false, 0, "errou");
        assertEquals(a, b);
    }
}
