package br.com.erm.mangue.forge.application.exception;

/** Lançada quando um ID de partida não corresponde a nenhuma partida ativa. */
public class PartidaNaoEncontradaException extends RuntimeException {
    public PartidaNaoEncontradaException(String mensagem) {
        super(mensagem);
    }
}
