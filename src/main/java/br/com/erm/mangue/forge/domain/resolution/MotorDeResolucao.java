package br.com.erm.mangue.forge.domain.resolution;

import br.com.erm.mangue.forge.domain.model.Combatente;

/**
 * Contrato de resolução de ações de combate (Strategy).
 *
 * <p>Cada sistema de jogo (D20, percentual, etc.) implementa esta interface em
 * {@code rules}; o domínio nunca conhece as regras concretas de nenhum jogo.</p>
 */
public interface MotorDeResolucao {

    /**
     * @param origem     combatente que executa a ação
     * @param alvo       combatente que recebe a ação
     * @param nomeDaAcao identificador genérico da ação/perícia usada (ex.: "forca", "mira")
     * @return o resultado calculado da ação
     */
    ResultadoAcao resolverAcao(Combatente origem, Combatente alvo, String nomeDaAcao);
}
