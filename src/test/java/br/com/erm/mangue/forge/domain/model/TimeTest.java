package br.com.erm.mangue.forge.domain.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class TimeTest {

    @Test
    void doisTimesComMesmoIdentificadorSaoIguais() {
        Time a = new Time("herois");
        Time b = new Time("herois");
        assertEquals(a, b);
    }

    @Test
    void identificadorEExposto() {
        Time time = new Time("monstros");
        assertEquals("monstros", time.identificador());
    }
}
