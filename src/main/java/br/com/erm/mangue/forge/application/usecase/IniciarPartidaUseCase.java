package br.com.erm.mangue.forge.application.usecase;

import br.com.erm.mangue.forge.domain.model.Combatente;
import br.com.erm.mangue.forge.domain.model.Partida;
import java.util.List;

/** Cria uma {@link Partida} a partir de uma lista de combatentes. */
public final class IniciarPartidaUseCase {

    /**
     * @param participantes   combatentes que entrarão na partida; não pode ser nula ou vazia
     * @param chaveIniciativa nome do atributo dinâmico usado para ordenar os turnos
     * @throws IllegalArgumentException se {@code participantes} for nula ou vazia
     */
    public Partida executar(List<Combatente> participantes, String chaveIniciativa) {
        if (participantes == null || participantes.isEmpty()) {
            throw new IllegalArgumentException("É necessário ao menos um combatente para iniciar uma partida.");
        }
        return new Partida(participantes, chaveIniciativa);
    }
}
