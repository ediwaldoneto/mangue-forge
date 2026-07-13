package br.com.erm.mangue.forge.application.usecase;

import br.com.erm.mangue.forge.domain.model.Combatente;
import br.com.erm.mangue.forge.domain.model.Partida;
import br.com.erm.mangue.forge.domain.resolution.MotorDeResolucao;
import br.com.erm.mangue.forge.domain.resolution.ResultadoAcao;

/**
 * Orquestra um ataque: delega o cálculo ao {@link MotorDeResolucao} injetado,
 * aplica o dano resultante ao alvo e avança o turno da {@link Partida}.
 */
public final class ProcessarAtaqueUseCase {

    private final MotorDeResolucao motorDeRegras;

    /** @param motorDeRegras estratégia de resolução de ações usada por esta instância (D20, percentual, etc.) */
    public ProcessarAtaqueUseCase(MotorDeResolucao motorDeRegras) {
        this.motorDeRegras = motorDeRegras;
    }

    /**
     * @param partida    partida em andamento; o atacante é o combatente da vez ({@link Partida#combatenteDaVez()})
     * @param alvo       combatente que recebe a ação
     * @param nomeDaAcao identificador genérico da ação/perícia usada
     * @return o resultado calculado pelo motor de regras
     */
    public ResultadoAcao executar(Partida partida, Combatente alvo, String nomeDaAcao) {
        Combatente atacante = partida.combatenteDaVez();
        ResultadoAcao resultado = motorDeRegras.resolverAcao(atacante, alvo, nomeDaAcao);

        if (resultado.sucesso()) {
            alvo.aplicarDano(resultado.valor());
        }
        partida.avancarTurno();
        return resultado;
    }
}
