package br.com.erm.mangue.forge.presentation.api;

import br.com.erm.mangue.forge.application.exception.CombatenteNaoEncontradoException;
import br.com.erm.mangue.forge.application.exception.PartidaNaoEncontradaException;
import br.com.erm.mangue.forge.domain.exception.NenhumCombatenteVivoException;
import br.com.erm.mangue.forge.presentation.api.dto.ErroResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/** Traduz exceções de domínio/aplicação em respostas HTTP {@code {"erro": "..."}}. */
@RestControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErroResponse> tratarArgumentoInvalido(IllegalArgumentException ex) {
        return ResponseEntity.badRequest().body(new ErroResponse(ex.getMessage()));
    }

    @ExceptionHandler({PartidaNaoEncontradaException.class, CombatenteNaoEncontradoException.class})
    public ResponseEntity<ErroResponse> tratarNaoEncontrado(RuntimeException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErroResponse(ex.getMessage()));
    }

    @ExceptionHandler(NenhumCombatenteVivoException.class)
    public ResponseEntity<ErroResponse> tratarSemCombatentesVivos(NenhumCombatenteVivoException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(new ErroResponse(ex.getMessage()));
    }
}
