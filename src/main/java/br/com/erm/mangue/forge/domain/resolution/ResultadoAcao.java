package br.com.erm.mangue.forge.domain.resolution;

/**
 * Resultado do cálculo de uma ação de combate por um {@link MotorDeResolucao}.
 *
 * @param sucesso   se a ação foi bem-sucedida
 * @param valor     dano ou margem de sucesso quando {@code sucesso} é {@code true}; {@code 0} em caso de falha
 * @param descricao texto legível descrevendo o resultado (útil para logs/CLI)
 */
public record ResultadoAcao(boolean sucesso, int valor, String descricao) {
}
