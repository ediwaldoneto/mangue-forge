# Documentação do Motor de Combate

Este documento cobre tudo que está implementado no Mangue Forge até agora:
o núcleo do domínio, os motores de regra de exemplo, buffs/debuffs e a API
REST. Para o histórico de decisões de design, veja as specs em
[`docs/superpowers/specs/`](superpowers/specs/).

## Índice

1. [Filosofia](#filosofia)
2. [Arquitetura completa](#arquitetura-completa)
3. [Conceitos do domínio](#conceitos-do-domínio)
4. [Resolução de ações (Strategy)](#resolução-de-ações-strategy)
5. [Buffs e debuffs](#buffs-e-debuffs)
6. [Casos de uso](#casos-de-uso)
7. [Exemplos de uso em código](#exemplos-de-uso-em-código)
8. [API REST](#api-rest)
9. [Estratégia de testes](#estratégia-de-testes)
10. [O que ainda falta](#o-que-ainda-falta)

## Filosofia

O motor separa duas coisas que a maioria dos projetos de RPG mistura:

- **O que é universal em qualquer combate por turnos**: quem tem vida, quem
  age em que ordem, o que acontece quando alguém aplica dano ou cura. Isso
  vive em `domain` e nunca muda, não importa qual jogo esteja sendo jogado.
- **O que é específico de um sistema de regras**: como um dado é rolado,
  como sucesso/falha é calculado, quais nomes de atributo existem. Isso vive
  em `rules`, como plugins que implementam um contrato do domínio.

`domain` nunca importa nada de `rules`. Se você apagar `rules/` inteira, o
domínio continua compilando sozinho — essa é a prova estrutural de que o
motor é agnóstico a qualquer RPG específico.

## Arquitetura completa

```
br.com.erm.mangue.forge
├── domain
│   ├── model        → Combatente, Time, AtributosDinamicos, PontosDeVida,
│   │                   EfeitoAtivo, TipoEfeito, Partida
│   ├── resolution   → MotorDeResolucao (Strategy), ResultadoAcao,
│   │                   FonteAleatoria, RandomFonteAleatoria
│   ├── turn         → FilaDeIniciativa
│   └── exception    → NenhumCombatenteVivoException
├── application
│   ├── usecase      → IniciarPartidaUseCase, ProcessarAtaqueUseCase
│   └── exception    → PartidaNaoEncontradaException, CombatenteNaoEncontradoException
├── rules                        (plugins de regras — fora do domínio, dependem dele)
│   ├── MotorDeResolucaoFactory  → escolhe MotorD20 ou MotorPercentual por nome
│   ├── d20          → MotorD20
│   └── percentual   → MotorPercentual
├── infrastructure
│   └── persistence  → PartidaAtiva, PartidaEmMemoriaRepository (in-memory)
└── presentation
    └── api          → PartidaController, ApiExceptionHandler, dto/*
```

## Conceitos do domínio

### `AtributosDinamicos`
Wrapper imutável sobre um `Map<String, Integer>`. Não conhece nenhum nome de
atributo específico — cada sistema de jogo define os seus (`"forca"`,
`"mira"`, `"carisma"`, o que fizer sentido). `obterValor(chave)` lança
`IllegalArgumentException` se a chave não existir.

### `PontosDeVida`
Imutável. `atual` sempre entre `0` e `maximo` (clamp automático).
`receberDano`/`curar` retornam uma nova instância — nunca mutam a atual.

### `Time`
Identifica o lado/facção de um combatente. Não é um enum fixo de dois lados
— uma partida pode ter três ou mais facções.

### `Combatente`
Entidade com `id`, `identificacao`, `time`, `atributos` (fixo durante a
partida) e `hp` (mutável, sempre reatribuído). Também guarda uma lista de
`EfeitoAtivo` em andamento (veja [Buffs e debuffs](#buffs-e-debuffs)).

### `Partida`
Gerencia a ordem de turnos via `FilaDeIniciativa`, ordenando por um atributo
de iniciativa configurável (ex.: `"agilidade"`). Cada instância é isolada —
nenhuma partida compartilha estado com outra, o que evita condições de
corrida se partidas diferentes rodarem em paralelo (relevante agora que
existem requisições HTTP concorrentes na API). `combatenteDaVez()` e
`avancarTurno()` excluem combatentes mortos ao recalcular uma nova rodada;
`buscarParticipante(id)` resolve um combatente pelo ID (usado pela API para
traduzir um `alvoId` recebido em JSON).

## Resolução de ações (Strategy)

```java
public interface MotorDeResolucao {
    ResultadoAcao resolverAcao(Combatente origem, Combatente alvo, String nomeDaAcao);
}
```

Uma ação é só um identificador genérico (`nomeDaAcao`) — o motor não sabe se
é "força", "mira" ou qualquer outra coisa. Quem calcula o resultado é a
implementação concreta:

- **`MotorD20`** (`rules.d20`): rola 1d20 + o atributo do atacante referenciado
  por `nomeDaAcao`, compara com uma dificuldade fixa. Sucesso se
  `rolagem + atributo >= dificuldade`; `valor` é a margem de sucesso.
- **`MotorPercentual`** (`rules.percentual`): rola 1d100, sucesso se a
  rolagem for `<=` o percentual do atacante na perícia referenciada por
  `nomeDaAcao`; `valor` é a margem de sucesso.

Ambos recebem uma `FonteAleatoria` (interface `rolar(int faces)`) por
injeção de construtor — em produção, `RandomFonteAleatoria` (apoiada em
`java.util.random.RandomGenerator`); nos testes, uma fake determinística.

`MotorDeResolucaoFactory` (também em `rules`, mas fora de `d20`/`percentual`)
compõe o motor certo a partir de uma string (`"d20"` ou `"percentual"`) — é
essa classe que a API usa para deixar o cliente escolher o sistema de regras.

## Buffs e debuffs

Efeitos de dano ou cura ao longo do tempo (veneno, regeneração), o tipo mais
comum em RPGs por turno.

```java
public enum TipoEfeito { DANO_POR_TURNO, CURA_POR_TURNO }

public final class EfeitoAtivo {
    // nome, tipo, valorPorTurno, duracaoRestante — imutável
    public EfeitoAtivo decrementarDuracao(); // nova instância com duração -1
    public boolean expirou();                // duracaoRestante <= 0
}
```

Um efeito é aplicado a um combatente com `combatente.aplicarEfeito(efeito)`.
O processamento é automático — `Partida` chama
`combatente.processarInicioDoTurno()` sempre que fica a vez de alguém (no
construtor, para o primeiro turno, e ao final de cada `avancarTurno()`).
Para cada efeito ativo: aplica o tick (dano ou cura), decrementa a duração,
e remove da lista os que expiraram. Combatentes sem efeitos não são afetados
— é um no-op.

Não há modificadores temporários de atributo (buff de força, etc.) ainda —
só dano/cura ao longo do tempo — e não há exposição desses efeitos via API
REST por enquanto (só no domínio).

## Casos de uso

```java
public final class IniciarPartidaUseCase {
    Partida executar(List<Combatente> participantes, String chaveIniciativa);
}

public final class ProcessarAtaqueUseCase {
    ProcessarAtaqueUseCase(MotorDeResolucao motorDeRegras);
    ResultadoAcao executar(Partida partida, Combatente alvo, String nomeDaAcao);
}
```

`ProcessarAtaqueUseCase` resolve a ação via o motor injetado, aplica o dano
resultante ao alvo (se `sucesso`) e avança o turno — sem saber se o motor é
D20, Percentual ou qualquer outro.

## Exemplos de uso em código

### Batalha simples, direto no domínio

```java
Combatente heroi = new Combatente("Heroi", new Time("herois"),
        new AtributosDinamicos(Map.of("forca", 5, "agilidade", 9)), 20);
Combatente monstro = new Combatente("Monstro", new Time("monstros"),
        new AtributosDinamicos(Map.of("forca", 3, "agilidade", 4)), 15);

Partida partida = new IniciarPartidaUseCase()
        .executar(List.of(heroi, monstro), "agilidade");

MotorDeResolucao regrasD20 = new MotorD20(new RandomFonteAleatoria(), 15);
ProcessarAtaqueUseCase ataque = new ProcessarAtaqueUseCase(regrasD20);

ResultadoAcao resultado = ataque.executar(partida, monstro, "forca");
```

Trocar `new MotorD20(...)` por `new MotorPercentual(new RandomFonteAleatoria())`
não exige nenhuma outra mudança — essa troca é a prova em código de que o
motor é agnóstico.

### Aplicando um efeito (veneno) e deixando o motor cuidar do resto

```java
monstro.aplicarEfeito(new EfeitoAtivo("Veneno", TipoEfeito.DANO_POR_TURNO, 5, 3));

// Da próxima vez que a Partida chegar na vez do "monstro" (via avancarTurno()
// ou já no turno atual, se for o caso), o veneno é processado automaticamente:
// aplica 5 de dano, decrementa a duração para 2, e continua ativo.
partida.avancarTurno();
```

## API REST

Duas rotas, ambas em `PartidaController`, servidas por um repositório em
memória (`PartidaEmMemoriaRepository`, `ConcurrentHashMap` por trás dos
panos — seguro para criar/consultar partidas diferentes simultaneamente).

### `POST /api/partidas`

Cria uma partida, escolhendo o motor de regras no próprio request.

```json
{
  "motor": "d20",
  "dificuldade": 12,
  "chaveIniciativa": "agilidade",
  "combatentes": [
    {"identificacao": "Heroi", "time": "herois", "atributos": {"forca": 5, "agilidade": 9}, "hpMaximo": 20},
    {"identificacao": "Monstro", "time": "monstros", "atributos": {"forca": 3, "agilidade": 4}, "hpMaximo": 15}
  ]
}
```

`motor` é `"d20"` ou `"percentual"`; `dificuldade` é obrigatória só para
`"d20"`. Resposta `201 Created`:

```json
{
  "partidaId": "3fa8...",
  "combatentes": [
    {"id": "a1b2...", "identificacao": "Heroi", "hpAtual": 20, "hpMaximo": 20},
    {"id": "c3d4...", "identificacao": "Monstro", "hpAtual": 15, "hpMaximo": 15}
  ],
  "combatenteDaVezId": "a1b2..."
}
```

### `POST /api/partidas/{partidaId}/ataques`

Processa um ataque do combatente da vez contra um alvo.

```json
{ "alvoId": "c3d4...", "nomeDaAcao": "forca" }
```

Resposta `200 OK`:

```json
{
  "sucesso": true,
  "valor": 8,
  "descricao": "Sucesso: 20 contra dificuldade 12",
  "alvoHpAtual": 7,
  "alvoMorreu": false,
  "combatenteDaVezId": "c3d4..."
}
```

`alvoMorreu` reflete `alvo.estaVivo()` após o ataque — o domínio não tem
conceito de "vitória de time"; quem decide que a batalha acabou é o cliente,
observando esse campo.

### Erros

| Situação | Status |
|---|---|
| Argumento inválido (lista de combatentes vazia, chave de atributo/ação inexistente, motor desconhecido, `dificuldade` ausente para `"d20"`) | 400 |
| `partidaId` não existe | 404 |
| `alvoId` não é participante daquela partida | 404 |
| Todos os combatentes morreram (caso extremo) | 409 |

Corpo do erro: `{"erro": "<mensagem>"}`.

### Testando manualmente

Uma collection do Insomnia pronta para importar está em
[`docs/insomnia/mangue-forge-insomnia.json`](insomnia/mangue-forge-insomnia.json),
com exemplos para os dois motores e para processar um ataque.

## Estratégia de testes

- **Domínio** (`domain`): JUnit puro, sem contexto Spring — testa Value
  Objects, `Combatente`, `Partida` e a fila de turnos isoladamente.
- **Motores de regra** (`rules`): usam uma `FonteAleatoria` fake retornando
  valores fixos, validando fórmulas de forma determinística.
- **API** (`presentation.api`): `@WebMvcTest` com `MockMvc`, mockando o
  repositório e a factory via `@MockitoBean` — testa serialização, status
  HTTP e mapeamento de erros sem subir o contexto Spring completo.

## O que ainda falta

- Modificadores temporários de atributo (buff/debuff de força, agilidade, etc.).
- Exposição de efeitos (buffs/debuffs) via API REST.
- Persistência em banco de dados (hoje é só em memória).
- CLI de demonstração.
- Mecanismos reais de concorrência (locks, `Atomic*`) — o desenho atual só
  evita estado global mutável, preparando o terreno para isso.
