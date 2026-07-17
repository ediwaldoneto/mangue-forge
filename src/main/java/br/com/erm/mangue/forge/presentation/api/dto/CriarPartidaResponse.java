package br.com.erm.mangue.forge.presentation.api.dto;

import java.util.List;
import java.util.UUID;

public record CriarPartidaResponse(UUID partidaId, List<CombatenteResponse> combatentes, UUID combatenteDaVezId) {
}
