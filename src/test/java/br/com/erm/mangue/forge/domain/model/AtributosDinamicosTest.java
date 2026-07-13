package br.com.erm.mangue.forge.domain.model;

import org.junit.jupiter.api.Test;
import java.util.HashMap;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.*;

class AtributosDinamicosTest {

    @Test
    void obterValorRetornaValorDaChaveExistente() {
        Map<String, Integer> status = new HashMap<>();
        status.put("forca", 7);
        AtributosDinamicos atributos = new AtributosDinamicos(status);

        assertEquals(7, atributos.obterValor("forca"));
    }

    @Test
    void obterValorDeChaveInexistenteLancaExcecao() {
        AtributosDinamicos atributos = new AtributosDinamicos(Map.of("mira", 60));
        assertThrows(IllegalArgumentException.class, () -> atributos.obterValor("carisma"));
    }

    @Test
    void possuiAtributoRetornaFalsoParaChaveAusente() {
        AtributosDinamicos atributos = new AtributosDinamicos(Map.of("mira", 60));
        assertFalse(atributos.possuiAtributo("carisma"));
        assertTrue(atributos.possuiAtributo("mira"));
    }

    @Test
    void mapaOriginalNaoPodeSerAlteradoAposConstrucao() {
        Map<String, Integer> status = new HashMap<>();
        status.put("forca", 7);
        AtributosDinamicos atributos = new AtributosDinamicos(status);

        status.put("forca", 999);
        assertEquals(7, atributos.obterValor("forca"));
    }
}
