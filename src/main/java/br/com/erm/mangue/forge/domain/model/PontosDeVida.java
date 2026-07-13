package br.com.erm.mangue.forge.domain.model;

/**
 * Rastreia os pontos de vida (HP) de um combatente.
 *
 * <p>Objeto de valor imutável: {@code atual} é sempre mantido no intervalo
 * {@code [0, maximo]} (clamp automático no construtor), e toda operação que
 * altera a vida retorna uma nova instância em vez de modificar a atual.</p>
 */
public final class PontosDeVida {

    private final int atual;
    private final int maximo;

    /**
     * @param atual valor inicial de vida; é ajustado automaticamente para o intervalo {@code [0, maximo]}
     * @param maximo capacidade máxima de vida
     * @throws IllegalArgumentException se {@code maximo} for menor ou igual a zero
     */
    public PontosDeVida(int atual, int maximo) {
        if (maximo <= 0) {
            throw new IllegalArgumentException("O HP máximo deve ser maior que zero.");
        }
        this.maximo = maximo;
        this.atual = Math.max(0, Math.min(atual, maximo));
    }

    public int getAtual() {
        return atual;
    }

    public int getMaximo() {
        return maximo;
    }

    /**
     * @param quantidade dano a subtrair da vida atual
     * @return nova instância com a vida reduzida, sem passar de zero
     * @throws IllegalArgumentException se {@code quantidade} for negativa
     */
    public PontosDeVida receberDano(int quantidade) {
        if (quantidade < 0) {
            throw new IllegalArgumentException("Dano não pode ser negativo.");
        }
        return new PontosDeVida(this.atual - quantidade, this.maximo);
    }

    /**
     * @param quantidade cura a somar à vida atual
     * @return nova instância com a vida aumentada, sem passar do máximo
     * @throws IllegalArgumentException se {@code quantidade} for negativa
     */
    public PontosDeVida curar(int quantidade) {
        if (quantidade < 0) {
            throw new IllegalArgumentException("Cura não pode ser negativa.");
        }
        return new PontosDeVida(this.atual + quantidade, this.maximo);
    }

    /** @return {@code true} se a vida atual chegou a zero. */
    public boolean estaMorto() {
        return this.atual == 0;
    }
}
