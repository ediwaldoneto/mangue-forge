package br.com.erm.mangue.forge.rules.d20;

import br.com.erm.mangue.forge.domain.model.Combatente;
import br.com.erm.mangue.forge.domain.resolution.FonteAleatoria;
import br.com.erm.mangue.forge.domain.resolution.MotorDeResolucao;
import br.com.erm.mangue.forge.domain.resolution.ResultadoAcao;

/**
 * Implementação de exemplo de {@link MotorDeResolucao} no estilo D20 (D&amp;D):
 * rola 1d20, soma o atributo do atacante indicado por {@code nomeDaAcao} e
 * compara o total contra uma dificuldade fixa.
 *
 * <p>Vive fora de {@code domain} propositalmente — é um plugin de regras, não
 * faz parte do motor agnóstico.</p>
 */
public final class MotorD20 implements MotorDeResolucao {

    private final FonteAleatoria fonte;
    private final int dificuldade;

    /**
     * @param fonte       fonte de aleatoriedade usada para rolar o dado
     * @param dificuldade valor mínimo que rolagem + atributo precisam somar para haver sucesso
     */
    public MotorD20(FonteAleatoria fonte, int dificuldade) {
        this.fonte = fonte;
        this.dificuldade = dificuldade;
    }

    /**
     * Rola 1d20, soma o atributo do atacante referenciado por {@code nomeDaAcao}
     * e compara o total com a dificuldade.
     *
     * @return sucesso com a margem ({@code total - dificuldade}) como {@code valor}, ou falha com {@code valor} zero
     */
    @Override
    public ResultadoAcao resolverAcao(Combatente origem, Combatente alvo, String nomeDaAcao) {
        int rolagem = fonte.rolar(20);
        int atributo = origem.getAtributos().obterValor(nomeDaAcao);
        int total = rolagem + atributo;

        if (total >= dificuldade) {
            int margem = total - dificuldade;
            return new ResultadoAcao(true, margem, "Sucesso: " + total + " contra dificuldade " + dificuldade);
        }
        return new ResultadoAcao(false, 0, "Falha: " + total + " contra dificuldade " + dificuldade);
    }
}
