package br.com.erm.mangue.forge.domain.turn;

import br.com.erm.mangue.forge.domain.model.AtributosDinamicos;
import br.com.erm.mangue.forge.domain.model.Combatente;
import br.com.erm.mangue.forge.domain.model.Time;
import org.junit.jupiter.api.Test;
import java.util.List;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.*;

class FilaDeIniciativaTest {

    private Combatente combatenteComAgilidade(String nome, int agilidade) {
        return new Combatente(nome, new Time("herois"),
                new AtributosDinamicos(Map.of("agilidade", agilidade)), 10);
    }

    @Test
    void ordenaDoMaiorParaOMenorPelaChaveDeIniciativa() {
        Combatente lento = combatenteComAgilidade("Lento", 3);
        Combatente rapido = combatenteComAgilidade("Rapido", 9);
        Combatente medio = combatenteComAgilidade("Medio", 6);

        List<Combatente> ordenados = new FilaDeIniciativa()
                .ordenar(List.of(lento, rapido, medio), "agilidade");

        assertEquals(List.of(rapido, medio, lento), ordenados);
    }

    @Test
    void excluiCombatentesMortosDaOrdenacao() {
        Combatente vivo = combatenteComAgilidade("Vivo", 5);
        Combatente morto = combatenteComAgilidade("Morto", 10);
        morto.aplicarDano(999);

        List<Combatente> ordenados = new FilaDeIniciativa()
                .ordenar(List.of(vivo, morto), "agilidade");

        assertEquals(List.of(vivo), ordenados);
    }
}
