package br.com.erm.mangue.forge.domain.model;

import br.com.erm.mangue.forge.domain.exception.NenhumCombatenteVivoException;
import org.junit.jupiter.api.Test;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.*;

class PartidaTest {

    private Combatente combatenteComAgilidade(String nome, int agilidade, int hp) {
        return new Combatente(nome, new Time("herois"),
                new AtributosDinamicos(Map.of("agilidade", agilidade)), hp);
    }

    @Test
    void combatenteDaVezERespeitaOrdemDeIniciativa() {
        Combatente rapido = combatenteComAgilidade("Rapido", 9, 10);
        Combatente lento = combatenteComAgilidade("Lento", 3, 10);

        Partida partida = new Partida(List.of(lento, rapido), "agilidade");

        assertEquals(rapido.getId(), partida.combatenteDaVez().getId());
    }

    @Test
    void avancarTurnoPassaParaOProximoDaFila() {
        Combatente rapido = combatenteComAgilidade("Rapido", 9, 10);
        Combatente lento = combatenteComAgilidade("Lento", 3, 10);

        Partida partida = new Partida(List.of(lento, rapido), "agilidade");
        partida.avancarTurno();

        assertEquals(lento.getId(), partida.combatenteDaVez().getId());
    }

    @Test
    void aposUltimoDaFilaNovaRodadaRecomecaPeloMaisRapido() {
        Combatente rapido = combatenteComAgilidade("Rapido", 9, 10);
        Combatente lento = combatenteComAgilidade("Lento", 3, 10);

        Partida partida = new Partida(List.of(lento, rapido), "agilidade");
        partida.avancarTurno(); // agora e' a vez do lento
        partida.avancarTurno(); // fila esvazia, nova rodada comeca

        assertEquals(rapido.getId(), partida.combatenteDaVez().getId());
    }

    @Test
    void combatenteMortoEExcluidoDaProximaRodada() {
        Combatente rapido = combatenteComAgilidade("Rapido", 9, 10);
        Combatente lento = combatenteComAgilidade("Lento", 3, 10);

        Partida partida = new Partida(List.of(lento, rapido), "agilidade");
        rapido.aplicarDano(999);

        partida.avancarTurno(); // remove o rapido (ja morto) da fila da rodada atual; fila fica so' com o lento
        partida.avancarTurno(); // fila esvazia, nova rodada e' recalculada -- se a exclusao falhar, o rapido (morto) voltaria a frente

        assertEquals(lento.getId(), partida.combatenteDaVez().getId());
    }

    @Test
    void quandoTodosMorremLancaExcecaoAoIniciarNovaRodada() {
        Combatente unico = combatenteComAgilidade("Unico", 5, 10);
        Partida partida = new Partida(List.of(unico), "agilidade");

        unico.aplicarDano(999);

        assertThrows(NenhumCombatenteVivoException.class, partida::avancarTurno);
    }

    @Test
    void buscarParticipanteRetornaCombatenteExistente() {
        Combatente unico = combatenteComAgilidade("Unico", 5, 10);
        Partida partida = new Partida(List.of(unico), "agilidade");

        assertTrue(partida.buscarParticipante(unico.getId()).isPresent());
    }

    @Test
    void buscarParticipanteRetornaVazioParaIdInexistente() {
        Combatente unico = combatenteComAgilidade("Unico", 5, 10);
        Partida partida = new Partida(List.of(unico), "agilidade");

        assertTrue(partida.buscarParticipante(UUID.randomUUID()).isEmpty());
    }

    @Test
    void efeitoEProcessadoAutomaticamenteNoPrimeiroTurno() {
        Combatente rapido = combatenteComAgilidade("Rapido", 9, 20);
        rapido.aplicarEfeito(new EfeitoAtivo("Veneno", TipoEfeito.DANO_POR_TURNO, 5, 2));
        Combatente lento = combatenteComAgilidade("Lento", 3, 10);

        new Partida(List.of(lento, rapido), "agilidade");

        assertEquals(15, rapido.getHp().getAtual());
    }

    @Test
    void efeitoEProcessadoAutomaticamenteAoAvancarParaOProximoTurno() {
        Combatente rapido = combatenteComAgilidade("Rapido", 9, 20);
        Combatente lento = combatenteComAgilidade("Lento", 3, 10);
        lento.aplicarEfeito(new EfeitoAtivo("Veneno", TipoEfeito.DANO_POR_TURNO, 4, 2));

        Partida partida = new Partida(List.of(lento, rapido), "agilidade");
        partida.avancarTurno(); // agora e' a vez do lento -- efeito dele deve ser processado

        assertEquals(6, lento.getHp().getAtual());
    }
}
