package br.com.erm.mangue.forge.presentation.api.dto;

import java.util.UUID;

public record CombatenteResponse(UUID id, String identificacao, int hpAtual, int hpMaximo) {
}
