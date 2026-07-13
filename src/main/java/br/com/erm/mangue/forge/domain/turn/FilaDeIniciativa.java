package br.com.erm.mangue.forge.domain.turn;

import br.com.erm.mangue.forge.domain.model.Combatente;
import java.util.Comparator;
import java.util.List;

/**
 * Ordena combatentes vivos por um atributo de iniciativa, do maior para o menor.
 *
 * <p>Não guarda estado próprio — pode ser reutilizada livremente entre
 * partidas e rodadas.</p>
 */
public final class FilaDeIniciativa {

    /**
     * @param combatentes     lista de combatentes a ordenar
     * @param chaveIniciativa nome do atributo dinâmico usado como critério de ordenação (ex.: "agilidade")
     * @return combatentes vivos, ordenados do maior para o menor valor do atributo
     */
    public List<Combatente> ordenar(List<Combatente> combatentes, String chaveIniciativa) {
        return combatentes.stream()
                .filter(Combatente::estaVivo)
                .sorted(Comparator.comparingInt(
                        (Combatente c) -> c.getAtributos().obterValor(chaveIniciativa)).reversed())
                .toList();
    }
}
