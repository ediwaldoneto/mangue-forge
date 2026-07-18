package br.com.erm.mangue.forge.domain.resolution;

import java.util.Random;
import java.util.random.RandomGenerator;

/**
 * Implementação de produção de {@link FonteAleatoria}, apoiada em
 * {@link RandomGenerator}. Nos testes, prefira uma fake determinística.
 *
 * <p>O construtor padrão usa {@link Random} (não {@code RandomGenerator.getDefault()})
 * de propósito: o algoritmo padrão de {@code RandomGenerator.getDefault()}
 * (LXM, ex.: "L32X64MixRandom") vive no módulo {@code jdk.random}, que
 * imagens JRE reduzidas via {@code jlink} (como {@code eclipse-temurin:17-jre})
 * podem não incluir. {@link Random} vive só em {@code java.base}, sempre disponível.</p>
 */
public final class RandomFonteAleatoria implements FonteAleatoria {

    private final RandomGenerator random;

    public RandomFonteAleatoria() {
        this(new Random());
    }

    public RandomFonteAleatoria(RandomGenerator random) {
        this.random = random;
    }

    @Override
    public int rolar(int faces) {
        return random.nextInt(faces) + 1;
    }
}
