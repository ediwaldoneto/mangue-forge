package br.com.erm.mangue.forge.domain.model;

import java.util.Map;
import java.util.HashMap;
import java.util.Collections;
import java.util.Optional;

/**
 * Envelope imutável sobre um conjunto arbitrário de atributos (força, agilidade,
 * mira, carisma, etc.).
 *
 * <p>Não conhece nenhum nome de atributo específico de um sistema de jogo —
 * isso é o que mantém o motor agnóstico ao RPG que o utiliza.</p>
 */
public final class AtributosDinamicos {

    private final Map<String, Integer> status;

    /** Copia o mapa recebido para uma versão imutável interna. */
    public AtributosDinamicos(Map<String, Integer> status) {
        this.status = Collections.unmodifiableMap(new HashMap<>(status));
    }

    /**
     * @param chave nome do atributo (ex.: "forca", "mira")
     * @return o valor associado à chave
     * @throws IllegalArgumentException se a chave não existir
     */
    public int obterValor(String chave) {
        return Optional.ofNullable(status.get(chave))
                .orElseThrow(() -> new IllegalArgumentException("Atributo '" + chave + "' não encontrado."));
    }

    public boolean possuiAtributo(String chave) {
        return status.containsKey(chave);
    }
}
