package br.com.erm.mangue.forge.rules.percentual;

import br.com.erm.mangue.forge.domain.model.Combatente;
import br.com.erm.mangue.forge.domain.resolution.FonteAleatoria;
import br.com.erm.mangue.forge.domain.resolution.MotorDeResolucao;
import br.com.erm.mangue.forge.domain.resolution.ResultadoAcao;

/**
 * Implementação de exemplo de {@link MotorDeResolucao} no estilo percentual
 * (BRP/Vampiro): rola 1d100 e compara contra a perícia do atacante indicada
 * por {@code nomeDaAcao}.
 *
 * <p>Vive fora de {@code domain} propositalmente — é um plugin de regras, não
 * faz parte do motor agnóstico.</p>
 */
public final class MotorPercentual implements MotorDeResolucao {

    private final FonteAleatoria fonte;

    /** @param fonte fonte de aleatoriedade usada para rolar o dado percentual (1d100) */
    public MotorPercentual(FonteAleatoria fonte) {
        this.fonte = fonte;
    }

    /**
     * Rola 1d100 e compara com a perícia do atacante referenciada por {@code nomeDaAcao}.
     * Sucesso se a rolagem for menor ou igual ao percentual da perícia.
     *
     * @return sucesso com a margem ({@code percentual - rolagem + 1}) como {@code valor}, ou falha com {@code valor} zero
     */
    @Override
    public ResultadoAcao resolverAcao(Combatente origem, Combatente alvo, String nomeDaAcao) {
        int rolagem = fonte.rolar(100);
        int percentual = origem.getAtributos().obterValor(nomeDaAcao);

        if (rolagem <= percentual) {
            int margem = percentual - rolagem + 1;
            return new ResultadoAcao(true, margem, "Sucesso: " + rolagem + " contra " + percentual + "%");
        }
        return new ResultadoAcao(false, 0, "Falha: " + rolagem + " contra " + percentual + "%");
    }
}
