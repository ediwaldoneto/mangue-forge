package br.com.erm.mangue.forge.infrastructure.persistence;

import br.com.erm.mangue.forge.domain.model.Partida;
import br.com.erm.mangue.forge.domain.resolution.MotorDeResolucao;

/** Associa uma {@link Partida} ao {@link MotorDeResolucao} escolhido para ela na criação. */
public record PartidaAtiva(Partida partida, MotorDeResolucao motor) {
}
