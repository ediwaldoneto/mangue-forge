package br.com.erm.mangue.forge.domain.model;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Um participante de uma {@link Partida}.
 *
 * <p>{@code atributos} é fixo durante toda a partida; {@code hp} e
 * {@code efeitosAtivos} são os únicos campos mutáveis, sempre substituídos
 * por novas instâncias imutáveis.</p>
 */
public final class Combatente {

    private final UUID id;
    private final String identificacao;
    private final Time time;
    private final AtributosDinamicos atributos;
    private PontosDeVida hp;
    private List<EfeitoAtivo> efeitosAtivos = List.of();

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

    /** @param efeito efeito de dano/cura ao longo do tempo a aplicar a este combatente */
    public void aplicarEfeito(EfeitoAtivo efeito) {
        List<EfeitoAtivo> atualizados = new ArrayList<>(efeitosAtivos);
        atualizados.add(efeito);
        this.efeitosAtivos = List.copyOf(atualizados);
    }

    public List<EfeitoAtivo> getEfeitosAtivos() {
        return efeitosAtivos;
    }

    /**
     * Aplica o tick de cada efeito ativo (dano ou cura), decrementa suas
     * durações e remove da lista os que expiraram.
     */
    public void processarInicioDoTurno() {
        List<EfeitoAtivo> restantes = new ArrayList<>();
        for (EfeitoAtivo efeito : efeitosAtivos) {
            aplicarTick(efeito);
            EfeitoAtivo decrementado = efeito.decrementarDuracao();
            if (!decrementado.expirou()) {
                restantes.add(decrementado);
            }
        }
        this.efeitosAtivos = List.copyOf(restantes);
    }

    private void aplicarTick(EfeitoAtivo efeito) {
        switch (efeito.getTipo()) {
            case DANO_POR_TURNO -> aplicarDano(efeito.getValorPorTurno());
            case CURA_POR_TURNO -> receberCura(efeito.getValorPorTurno());
        }
    }
}
