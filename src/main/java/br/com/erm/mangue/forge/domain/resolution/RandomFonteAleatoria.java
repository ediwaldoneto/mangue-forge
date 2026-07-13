package br.com.erm.mangue.forge.domain.resolution;

import java.util.random.RandomGenerator;

/**
 * Implementação de produção de {@link FonteAleatoria}, apoiada em
 * {@link RandomGenerator}. Nos testes, prefira uma fake determinística.
 */
public final class RandomFonteAleatoria implements FonteAleatoria {

    private final RandomGenerator random;

    public RandomFonteAleatoria() {
        this(RandomGenerator.getDefault());
    }

    public RandomFonteAleatoria(RandomGenerator random) {
        this.random = random;
    }

    @Override
    public int rolar(int faces) {
        return random.nextInt(faces) + 1;
    }
}
