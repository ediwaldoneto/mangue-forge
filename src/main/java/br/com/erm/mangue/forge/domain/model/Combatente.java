package br.com.erm.mangue.forge.domain.model;

import java.util.UUID;

/**
 * Um participante de uma {@link Partida}.
 *
 * <p>{@code atributos} é fixo durante toda a partida; {@code hp} é o único
 * campo mutável, sempre substituído por uma nova instância imutável de
 * {@link PontosDeVida}.</p>
 */
public final class Combatente {

    private final UUID id;
    private final String identificacao;
    private final Time time;
    private final AtributosDinamicos atributos;
    private PontosDeVida hp;

    public Combatente(String identificacao, Time time, AtributosDinamicos atributos, int hpMaximo) {
        this.id = UUID.randomUUID();
        this.identificacao = identificacao;
        this.time = time;
        this.atributos = atributos;
        this.hp = new PontosDeVida(hpMaximo, hpMaximo);
    }

    public UUID getId() {
        return id;
    }

    public String getIdentificacao() {
        return identificacao;
    }

    public Time getTime() {
        return time;
    }

    public AtributosDinamicos getAtributos() {
        return atributos;
    }

    public PontosDeVida getHp() {
        return hp;
    }

    /** @param quantidade dano a aplicar; delega o clamp de vida a {@link PontosDeVida#receberDano} */
    public void aplicarDano(int quantidade) {
        this.hp = this.hp.receberDano(quantidade);
    }

    /** @param quantidade cura a aplicar; delega o clamp de vida a {@link PontosDeVida#curar} */
    public void receberCura(int quantidade) {
        this.hp = this.hp.curar(quantidade);
    }

    /** @return {@code true} se a vida atual for maior que zero. */
    public boolean estaVivo() {
        return !this.hp.estaMorto();
    }
}
