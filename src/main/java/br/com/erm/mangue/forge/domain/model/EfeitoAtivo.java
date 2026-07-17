package br.com.erm.mangue.forge.domain.model;

/**
 * Um efeito de dano ou cura ao longo do tempo aplicado a um {@link Combatente}
 * (veneno, regeneração, etc.).
 *
 * <p>Imutável, seguindo o mesmo padrão de {@link PontosDeVida}: avançar no
 * tempo ({@link #decrementarDuracao()}) retorna uma nova instância.
 * {@code duracaoRestante == 0} é um estado interno válido (acabou de expirar);
 * só valores negativos são inválidos.</p>
 */
public final class EfeitoAtivo {

    private final String nome;
    private final TipoEfeito tipo;
    private final int valorPorTurno;
    private final int duracaoRestante;

    /**
     * @throws IllegalArgumentException se {@code valorPorTurno} ou {@code duracaoRestante} forem negativos
     */
    public EfeitoAtivo(String nome, TipoEfeito tipo, int valorPorTurno, int duracaoRestante) {
        if (valorPorTurno < 0) {
            throw new IllegalArgumentException("valorPorTurno não pode ser negativo.");
        }
        if (duracaoRestante < 0) {
            throw new IllegalArgumentException("duracaoRestante não pode ser negativa.");
        }
        this.nome = nome;
        this.tipo = tipo;
        this.valorPorTurno = valorPorTurno;
        this.duracaoRestante = duracaoRestante;
    }

    public String getNome() {
        return nome;
    }

    public TipoEfeito getTipo() {
        return tipo;
    }

    public int getValorPorTurno() {
        return valorPorTurno;
    }

    public int getDuracaoRestante() {
        return duracaoRestante;
    }

    /** @return nova instância com a duração reduzida em 1. */
    public EfeitoAtivo decrementarDuracao() {
        return new EfeitoAtivo(nome, tipo, valorPorTurno, duracaoRestante - 1);
    }

    /** @return {@code true} se a duração restante chegou a zero ou menos. */
    public boolean expirou() {
        return duracaoRestante <= 0;
    }
}
