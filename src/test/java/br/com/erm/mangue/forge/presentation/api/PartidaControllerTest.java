package br.com.erm.mangue.forge.presentation.api;

import br.com.erm.mangue.forge.application.exception.PartidaNaoEncontradaException;
import br.com.erm.mangue.forge.domain.model.AtributosDinamicos;
import br.com.erm.mangue.forge.domain.model.Combatente;
import br.com.erm.mangue.forge.domain.model.Partida;
import br.com.erm.mangue.forge.domain.model.Time;
import br.com.erm.mangue.forge.domain.resolution.MotorDeResolucao;
import br.com.erm.mangue.forge.domain.resolution.ResultadoAcao;
import br.com.erm.mangue.forge.infrastructure.persistence.PartidaAtiva;
import br.com.erm.mangue.forge.infrastructure.persistence.PartidaEmMemoriaRepository;
import br.com.erm.mangue.forge.rules.MotorDeResolucaoFactory;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PartidaController.class)
class PartidaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private PartidaEmMemoriaRepository repositorio;

    @MockitoBean
    private MotorDeResolucaoFactory motorDeResolucaoFactory;

    private static class MotorFixo implements MotorDeResolucao {
        private final ResultadoAcao resultadoFixo;

        MotorFixo(ResultadoAcao resultadoFixo) {
            this.resultadoFixo = resultadoFixo;
        }

        @Override
        public ResultadoAcao resolverAcao(Combatente origem, Combatente alvo, String nomeDaAcao) {
            return resultadoFixo;
        }
    }

    @Test
    void criarPartidaRetorna201ComCombatentesEQuemComecaOTurno() throws Exception {
        when(motorDeResolucaoFactory.criar(eq("d20"), eq(12)))
                .thenReturn(new MotorFixo(new ResultadoAcao(true, 1, "ok")));

        String corpo = """
                {
                  "motor": "d20",
                  "dificuldade": 12,
                  "chaveIniciativa": "agilidade",
                  "combatentes": [
                    {"identificacao": "Heroi", "time": "herois", "atributos": {"forca": 5, "agilidade": 9}, "hpMaximo": 20},
                    {"identificacao": "Monstro", "time": "monstros", "atributos": {"forca": 3, "agilidade": 4}, "hpMaximo": 15}
                  ]
                }
                """;

        mockMvc.perform(post("/api/partidas").contentType("application/json").content(corpo))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.partidaId").exists())
                .andExpect(jsonPath("$.combatentes.length()").value(2))
                .andExpect(jsonPath("$.combatenteDaVezId").exists());

        verify(repositorio).salvar(any());
    }

    @Test
    void criarPartidaComListaVaziaRetorna400() throws Exception {
        String corpo = """
                {"motor": "d20", "dificuldade": 12, "chaveIniciativa": "agilidade", "combatentes": []}
                """;

        mockMvc.perform(post("/api/partidas").contentType("application/json").content(corpo))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.erro").exists());
    }

    @Test
    void ataqueEmPartidaInexistenteRetorna404() throws Exception {
        UUID partidaId = UUID.randomUUID();
        when(repositorio.buscarPorId(partidaId))
                .thenThrow(new PartidaNaoEncontradaException("Partida não encontrada: " + partidaId));

        String corpo = """
                {"alvoId": "%s", "nomeDaAcao": "forca"}
                """.formatted(UUID.randomUUID());

        mockMvc.perform(post("/api/partidas/" + partidaId + "/ataques")
                        .contentType("application/json").content(corpo))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.erro").exists());
    }

    @Test
    void ataqueComAlvoInexistenteNaPartidaRetorna404() throws Exception {
        Combatente atacante = new Combatente("A", new Time("herois"),
                new AtributosDinamicos(Map.of("agilidade", 9, "forca", 5)), 10);
        Combatente outro = new Combatente("B", new Time("monstros"),
                new AtributosDinamicos(Map.of("agilidade", 3)), 10);
        Partida partida = new Partida(List.of(atacante, outro), "agilidade");
        MotorFixo motor = new MotorFixo(new ResultadoAcao(true, 5, "ok"));
        when(repositorio.buscarPorId(partida.getId())).thenReturn(new PartidaAtiva(partida, motor));

        String corpo = """
                {"alvoId": "%s", "nomeDaAcao": "forca"}
                """.formatted(UUID.randomUUID());

        mockMvc.perform(post("/api/partidas/" + partida.getId() + "/ataques")
                        .contentType("application/json").content(corpo))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.erro").exists());
    }

    @Test
    void ataqueComSucessoRetorna200ComResultado() throws Exception {
        Combatente atacante = new Combatente("A", new Time("herois"),
                new AtributosDinamicos(Map.of("agilidade", 9, "forca", 5)), 10);
        Combatente alvo = new Combatente("B", new Time("monstros"),
                new AtributosDinamicos(Map.of("agilidade", 3)), 10);
        Partida partida = new Partida(List.of(atacante, alvo), "agilidade");
        MotorFixo motor = new MotorFixo(new ResultadoAcao(true, 6, "acertou"));
        when(repositorio.buscarPorId(partida.getId())).thenReturn(new PartidaAtiva(partida, motor));

        String corpo = """
                {"alvoId": "%s", "nomeDaAcao": "forca"}
                """.formatted(alvo.getId());

        mockMvc.perform(post("/api/partidas/" + partida.getId() + "/ataques")
                        .contentType("application/json").content(corpo))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.sucesso").value(true))
                .andExpect(jsonPath("$.alvoHpAtual").value(4))
                .andExpect(jsonPath("$.alvoMorreu").value(false));
    }
}
