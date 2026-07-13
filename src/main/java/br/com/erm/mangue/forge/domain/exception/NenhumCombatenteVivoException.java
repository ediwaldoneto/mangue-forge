package br.com.erm.mangue.forge.domain.exception;

/**
 * Lançada quando uma partida tenta iniciar uma nova rodada sem nenhum
 * combatente vivo entre os participantes.
 */
public class NenhumCombatenteVivoException extends IllegalStateException {
    public NenhumCombatenteVivoException(String mensagem) {
        super(mensagem);
    }
}
