package br.com.erm.mangue.forge.domain.model;

/**
 * Identifica o lado/facção de um {@link Combatente} dentro de uma partida.
 *
 * <p>Não é um enum fixo de dois lados — uma partida pode ter três ou mais
 * times/facções simultaneamente.</p>
 */
public record Time(String identificador) {
}
