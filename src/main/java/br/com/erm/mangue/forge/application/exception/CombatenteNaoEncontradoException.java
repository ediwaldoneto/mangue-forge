package br.com.erm.mangue.forge.application.exception;

/** Lançada quando um ID de combatente não corresponde a nenhum participante da partida informada. */
public class CombatenteNaoEncontradoException extends RuntimeException {
    public CombatenteNaoEncontradoException(String mensagem) {
        super(mensagem);
    }
}
