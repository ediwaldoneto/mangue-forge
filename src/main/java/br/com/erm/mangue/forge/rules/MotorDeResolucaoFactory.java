package br.com.erm.mangue.forge.rules;

import br.com.erm.mangue.forge.domain.resolution.FonteAleatoria;
import br.com.erm.mangue.forge.domain.resolution.MotorDeResolucao;
import br.com.erm.mangue.forge.domain.resolution.RandomFonteAleatoria;
import br.com.erm.mangue.forge.rules.d20.MotorD20;
import br.com.erm.mangue.forge.rules.percentual.MotorPercentual;
import org.springframework.stereotype.Component;

/**
 * Compõe um {@link MotorDeResolucao} concreto a partir de um nome de motor,
 * usado pela API para deixar o cliente escolher o sistema de regras.
 */
@Component
public final class MotorDeResolucaoFactory {

    /**
     * @param motor       {@code "d20"} ou {@code "percentual"}
     * @param dificuldade obrigatório quando {@code motor} é {@code "d20"}; ignorado para {@code "percentual"}
     * @throws IllegalArgumentException se {@code motor} for desconhecido, ou se {@code dificuldade} faltar para {@code "d20"}
     */
    public MotorDeResolucao criar(String motor, Integer dificuldade) {
        FonteAleatoria fonte = new RandomFonteAleatoria();
        return switch (motor) {
            case "d20" -> criarD20(fonte, dificuldade);
            case "percentual" -> new MotorPercentual(fonte);
            default -> throw new IllegalArgumentException("Motor desconhecido: " + motor);
        };
    }

    private MotorDeResolucao criarD20(FonteAleatoria fonte, Integer dificuldade) {
        if (dificuldade == null) {
            throw new IllegalArgumentException("Campo 'dificuldade' é obrigatório para o motor d20.");
        }
        return new MotorD20(fonte, dificuldade);
    }
}
