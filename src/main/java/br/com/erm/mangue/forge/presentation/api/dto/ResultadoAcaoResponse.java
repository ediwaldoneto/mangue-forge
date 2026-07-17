package br.com.erm.mangue.forge.presentation.api.dto;

import java.util.UUID;

public record ResultadoAcaoResponse(boolean sucesso, int valor, String descricao, int alvoHpAtual,
                                     boolean alvoMorreu, UUID combatenteDaVezId) {
}
