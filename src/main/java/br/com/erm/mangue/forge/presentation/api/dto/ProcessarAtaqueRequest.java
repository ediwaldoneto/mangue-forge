package br.com.erm.mangue.forge.presentation.api.dto;

import java.util.UUID;

public record ProcessarAtaqueRequest(UUID alvoId, String nomeDaAcao) {
}
