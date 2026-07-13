package br.com.erm.mangue.forge.domain.model;

import br.com.erm.mangue.forge.domain.exception.NenhumCombatenteVivoException;
import br.com.erm.mangue.forge.domain.turn.FilaDeIniciativa;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;
import java.util.UUID;

/**
 * Gerencia a ordem de turnos de uma batalha entre múltiplos {@link Combatente}.
 *
 * <p>Cada instância é completamente independente — nenhum estado é
 * compartilhado entre partidas diferentes, o que evita condições de corrida
 * caso partidas distintas rodem em paralelo.</p>
 */
public final class Partida {

    private final UUID id;
    private final List<Combatente> participantes;
    private final String chaveIniciativa;
    private final Deque<UUID> ordemDoTurno;

    /**
     * @param participantes   combatentes da partida; ao menos um deve estar vivo
     * @param chaveIniciativa nome do atributo dinâmico usado para ordenar os turnos (ex.: "agilidade")
     * @throws br.com.erm.mangue.forge.domain.exception.NenhumCombatenteVivoException se nenhum participante estiver vivo
     */
    public Partida(List<Combatente> participantes, String chaveIniciativa) {
        this.id = UUID.randomUUID();
        this.participantes = List.copyOf(participantes);
        this.chaveIniciativa = chaveIniciativa;
        this.ordemDoTurno = new ArrayDeque<>();
        iniciarNovaRodada();
    }

    public UUID getId() {
        return id;
    }

    /** @return o combatente cujo turno é o atual. */
    public Combatente combatenteDaVez() {
        return buscarPorId(ordemDoTurno.peekFirst());
    }

    /**
     * Avança para o próximo combatente da fila. Quando a fila se esvazia, uma
     * nova rodada é iniciada, recalculando a ordem e excluindo combatentes mortos.
     *
     * @throws br.com.erm.mangue.forge.domain.exception.NenhumCombatenteVivoException se nenhum combatente restar vivo
     */
    public void avancarTurno() {
        ordemDoTurno.pollFirst();
        if (ordemDoTurno.isEmpty()) {
            iniciarNovaRodada();
        }
    }

    private void iniciarNovaRodada() {
        List<Combatente> ordenados = new FilaDeIniciativa().ordenar(participantes, chaveIniciativa);
        if (ordenados.isEmpty()) {
            throw new NenhumCombatenteVivoException("Nenhum combatente vivo para continuar a partida.");
        }
        ordemDoTurno.clear();
        ordenados.forEach(c -> ordemDoTurno.addLast(c.getId()));
    }

    private Combatente buscarPorId(UUID idProcurado) {
        return participantes.stream()
                .filter(c -> c.getId().equals(idProcurado))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Combatente não encontrado na partida."));
    }
}
