package br.com.erm.mangue.forge.presentation.api;

import br.com.erm.mangue.forge.application.exception.CombatenteNaoEncontradoException;
import br.com.erm.mangue.forge.application.usecase.IniciarPartidaUseCase;
import br.com.erm.mangue.forge.application.usecase.ProcessarAtaqueUseCase;
import br.com.erm.mangue.forge.domain.model.AtributosDinamicos;
import br.com.erm.mangue.forge.domain.model.Combatente;
import br.com.erm.mangue.forge.domain.model.Partida;
import br.com.erm.mangue.forge.domain.model.Time;
import br.com.erm.mangue.forge.domain.resolution.MotorDeResolucao;
import br.com.erm.mangue.forge.domain.resolution.ResultadoAcao;
import br.com.erm.mangue.forge.infrastructure.persistence.PartidaAtiva;
import br.com.erm.mangue.forge.infrastructure.persistence.PartidaEmMemoriaRepository;
import br.com.erm.mangue.forge.presentation.api.dto.CombatenteResponse;
import br.com.erm.mangue.forge.presentation.api.dto.CriarPartidaRequest;
import br.com.erm.mangue.forge.presentation.api.dto.CriarPartidaResponse;
import br.com.erm.mangue.forge.presentation.api.dto.ProcessarAtaqueRequest;
import br.com.erm.mangue.forge.presentation.api.dto.ResultadoAcaoResponse;
import br.com.erm.mangue.forge.rules.MotorDeResolucaoFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

/** Expõe {@link IniciarPartidaUseCase} e {@link ProcessarAtaqueUseCase} via HTTP. */
@RestController
@RequestMapping("/api/partidas")
public class PartidaController {

    private final PartidaEmMemoriaRepository repositorio;
    private final MotorDeResolucaoFactory motorDeResolucaoFactory;

    public PartidaController(PartidaEmMemoriaRepository repositorio, MotorDeResolucaoFactory motorDeResolucaoFactory) {
        this.repositorio = repositorio;
        this.motorDeResolucaoFactory = motorDeResolucaoFactory;
    }

    @PostMapping
    public ResponseEntity<CriarPartidaResponse> criarPartida(@RequestBody CriarPartidaRequest request) {
        List<Combatente> combatentes = request.combatentes().stream()
                .map(c -> new Combatente(c.identificacao(), new Time(c.time()),
                        new AtributosDinamicos(c.atributos()), c.hpMaximo()))
                .toList();

        Partida partida = new IniciarPartidaUseCase().executar(combatentes, request.chaveIniciativa());
        MotorDeResolucao motor = motorDeResolucaoFactory.criar(request.motor(), request.dificuldade());
        repositorio.salvar(new PartidaAtiva(partida, motor));

        List<CombatenteResponse> combatentesResponse = combatentes.stream()
                .map(c -> new CombatenteResponse(c.getId(), c.getIdentificacao(), c.getHp().getAtual(), c.getHp().getMaximo()))
                .toList();

        CriarPartidaResponse response = new CriarPartidaResponse(
                partida.getId(), combatentesResponse, partida.combatenteDaVez().getId());

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/{partidaId}/ataques")
    public ResponseEntity<ResultadoAcaoResponse> processarAtaque(
            @PathVariable UUID partidaId, @RequestBody ProcessarAtaqueRequest request) {
        PartidaAtiva partidaAtiva = repositorio.buscarPorId(partidaId);
        Combatente alvo = partidaAtiva.partida().buscarParticipante(request.alvoId())
                .orElseThrow(() -> new CombatenteNaoEncontradoException(
                        "Combatente não encontrado na partida: " + request.alvoId()));

        ProcessarAtaqueUseCase useCase = new ProcessarAtaqueUseCase(partidaAtiva.motor());
        ResultadoAcao resultado = useCase.executar(partidaAtiva.partida(), alvo, request.nomeDaAcao());

        ResultadoAcaoResponse response = new ResultadoAcaoResponse(
                resultado.sucesso(), resultado.valor(), resultado.descricao(),
                alvo.getHp().getAtual(), !alvo.estaVivo(),
                partidaAtiva.partida().combatenteDaVez().getId());

        return ResponseEntity.ok(response);
    }
}
