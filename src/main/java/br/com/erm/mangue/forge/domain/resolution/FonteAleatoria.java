package br.com.erm.mangue.forge.domain.resolution;

/**
 * Abstrai a geração de números aleatórios usada pelos motores de regra,
 * permitindo testes determinísticos com implementações fake.
 */
public interface FonteAleatoria {

    /**
     * @param faces número de lados do dado (ex.: 20 para 1d20, 100 para 1d100)
     * @return um valor entre 1 e {@code faces}, inclusive
     */
    int rolar(int faces);
}
