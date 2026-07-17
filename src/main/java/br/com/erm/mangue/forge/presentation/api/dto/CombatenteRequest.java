package br.com.erm.mangue.forge.presentation.api.dto;

import java.util.Map;

public record CombatenteRequest(String identificacao, String time, Map<String, Integer> atributos, int hpMaximo) {
}
