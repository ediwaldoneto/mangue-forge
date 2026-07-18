package br.com.erm.mangue.forge.presentation.cli;

import br.com.erm.mangue.forge.application.usecase.IniciarPartidaUseCase;
import br.com.erm.mangue.forge.application.usecase.ProcessarAtaqueUseCase;
import br.com.erm.mangue.forge.domain.model.AtributosDinamicos;

import br.com.erm.mangue.forge.domain.model.Combatente;
import br.com.erm.mangue.forge.domain.model.Partida;
import br.com.erm.mangue.forge.domain.model.Time;
import br.com.erm.mangue.forge.domain.resolution.RandomFonteAleatoria;
import br.com.erm.mangue.forge.domain.resolution.ResultadoAcao;
import br.com.erm.mangue.forge.rules.d20.MotorD20;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

/**
 * Demonstração de ponta a ponta do motor de combate: uma batalha automática
 * entre dois combatentes fixos, usando o motor D20, imprimindo cada turno no
 * console. Classe independente de Spring — só usa o domínio e os motores de
 * regra, que já não dependem de nenhum framework.
 */
public final class BatalhaCliRunner {

    private static final Logger log = LoggerFactory.getLogger(BatalhaCliRunner.class);

    private static final int DIFICULDADE = 12;
    private static final int LIMITE_DE_TURNOS = 200;
    private static final String ATRIBUTO_FORCA = "forca";
    private static final String ATRIBUTO_AGILIDADE = "agilidade";

    public static void main(String[] args) {
        Combatente heroi = new Combatente("Heroi", new Time("herois"),
                new AtributosDinamicos(Map.of(ATRIBUTO_FORCA, 5, ATRIBUTO_AGILIDADE, 9)), 20);
        Combatente monstro = new Combatente("Monstro", new Time("monstros"),
                new AtributosDinamicos(Map.of(ATRIBUTO_FORCA, 3, ATRIBUTO_AGILIDADE, 4)), 15);

        Partida partida = new IniciarPartidaUseCase()
                .executar(List.of(heroi, monstro), ATRIBUTO_AGILIDADE);

        ProcessarAtaqueUseCase ataque = new ProcessarAtaqueUseCase(
                new MotorD20(new RandomFonteAleatoria(), DIFICULDADE));

        for (int turno = 1; turno <= LIMITE_DE_TURNOS; turno++) {
            Combatente atacante = partida.combatenteDaVez();
            Combatente alvo = atacante == heroi ? monstro : heroi;

            ResultadoAcao resultado = ataque.executar(partida, alvo, ATRIBUTO_FORCA);

            log.info("[Turno {}] {} ataca {}: {} (HP de {}: {}/{})",
                    turno, atacante.getIdentificacao(), alvo.getIdentificacao(),
                    resultado.descricao(), alvo.getIdentificacao(),
                    alvo.getHp().getAtual(), alvo.getHp().getMaximo());

            if (!alvo.estaVivo()) {
                log.info("{} venceu a batalha!", atacante.getIdentificacao());
                return;
            }
        }

        log.info("Limite de turnos atingido sem um vencedor.");
    }
}
