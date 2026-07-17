package br.com.erm.mangue.forge.presentation.api.dto;

import java.util.List;

public record CriarPartidaRequest(String motor, Integer dificuldade, String chaveIniciativa,
                                   List<CombatenteRequest> combatentes) {
}
