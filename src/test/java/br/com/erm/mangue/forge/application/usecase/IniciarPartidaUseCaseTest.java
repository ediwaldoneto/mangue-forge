package br.com.erm.mangue.forge.application.usecase;

import br.com.erm.mangue.forge.domain.model.AtributosDinamicos;
import br.com.erm.mangue.forge.domain.model.Combatente;
import br.com.erm.mangue.forge.domain.model.Partida;
import br.com.erm.mangue.forge.domain.model.Time;
import org.junit.jupiter.api.Test;
import java.util.List;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.*;

class IniciarPartidaUseCaseTest {

    @Test
    void iniciaPartidaComListaValida() {
        Combatente c1 = new Combatente("A", new Time("herois"), new AtributosDinamicos(Map.of("agilidade", 5)), 10);
        Combatente c2 = new Combatente("B", new Time("monstros"), new AtributosDinamicos(Map.of("agilidade", 8)), 10);

        Partida partida = new IniciarPartidaUseCase().executar(List.of(c1, c2), "agilidade");

        assertNotNull(partida);
        assertEquals(c2.getId(), partida.combatenteDaVez().getId());
    }

    @Test
    void listaVaziaLancaExcecao() {
        assertThrows(IllegalArgumentException.class,
                () -> new IniciarPartidaUseCase().executar(List.of(), "agilidade"));
    }
}
