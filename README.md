# Mangue Forge

**Um motor de combate por turnos que não conhece regra de nenhum RPG.**

Mangue Forge é o *coração* de um sistema de combate — não o jogo em si. Ele resolve turnos, vida, times e ordem de iniciativa de forma totalmente agnóstica ao sistema de regras usado. Quer rodar D&D? Vampiro: A Máscara? Um RPG cyberpunk inventado por você? O motor não muda uma linha — só a regra plugada nele muda.

```
"Se você apagar a pasta rules/ inteira, o domain/ continua compilando sozinho."
```
Essa é a prova de que o motor é, de fato, agnóstico.

## A ideia

A maioria dos motores de RPG (ou tutoriais sobre o assunto) mistura duas coisas que deveriam estar separadas: **as regras de um jogo específico** (dados, fórmulas, atributos com nomes fixos como "força" ou "magia") e **a mecânica universal de qualquer combate por turnos** (quem age, em que ordem, o que acontece com a vida de quem).

O Mangue Forge separa isso de propósito, usando conceitos de **Domain-Driven Design** e o princípio **Aberto/Fechado** do SOLID:

- O **domínio** (`domain`) só sabe o essencial de qualquer combate: combatentes, vida, atributos dinâmicos, ordem de turnos e o *contrato* de como uma ação é resolvida.
- As **regras de um jogo** (`rules`) vivem fora do domínio e o implementam — nunca o contrário. Hoje existem dois exemplos, provando que a estratégia é realmente intercambiável:
  - **D20** (estilo D&D): rola 1d20 + atributo vs. dificuldade.
  - **Percentual** (estilo BRP/Vampiro): rola 1d100 contra uma perícia.

## Arquitetura

```
br.com.erm.mangue.forge
├── domain
│   ├── model        → Combatente, Time, AtributosDinamicos, PontosDeVida, Partida
│   ├── resolution   → MotorDeResolucao (Strategy), ResultadoAcao, FonteAleatoria
│   ├── turn         → FilaDeIniciativa
│   └── exception
├── application
│   └── usecase      → IniciarPartidaUseCase, ProcessarAtaqueUseCase
└── rules                       (plugins de regras — fora do domínio, dependem dele)
    ├── d20          → MotorD20
    └── percentual   → MotorPercentual
```

**Regra de dependência:** `domain` nunca importa nada de `rules`. Qualquer sistema de jogo novo só precisa implementar a interface `MotorDeResolucao` — o motor de turnos, a vida e a ordenação de iniciativa continuam os mesmos.

### Peças-chave

| Conceito | Papel |
|---|---|
| `AtributosDinamicos` | Atributos livres (`Map<String, Integer>`), sem nomes fixos — cada jogo define os seus. |
| `Combatente` | Entidade com vida (`PontosDeVida`), atributos e time; único estado mutável é a vida. |
| `Partida` | Gerencia a fila de turnos por iniciativa; isolada — nenhuma partida compartilha estado com outra. |
| `MotorDeResolucao` | O contrato de Strategy que cada sistema de jogo implementa para calcular sucesso/dano. |
| `FonteAleatoria` | Abstrai a geração de números aleatórios, permitindo testes 100% determinísticos. |

## Exemplo

```java
Combatente heroi = new Combatente("Heroi", new Time("herois"),
        new AtributosDinamicos(Map.of("forca", 5, "agilidade", 9)), 20);
Combatente monstro = new Combatente("Monstro", new Time("monstros"),
        new AtributosDinamicos(Map.of("forca", 3, "agilidade", 4)), 15);

Partida partida = new IniciarPartidaUseCase()
        .executar(List.of(heroi, monstro), "agilidade");

// Troque o motor de resolução sem tocar em Partida, Combatente ou no caso de uso
MotorDeResolucao regrasD20 = new MotorD20(new RandomFonteAleatoria(), 15);
ProcessarAtaqueUseCase ataque = new ProcessarAtaqueUseCase(regrasD20);

ResultadoAcao resultado = ataque.executar(partida, monstro, "forca");
```

Trocar `MotorD20` por `MotorPercentual` não exige nenhuma outra mudança — é essa a prova em código de que o motor é agnóstico.

## Rodando os testes

```bash
export JAVA_HOME="/caminho/para/seu/jdk-17-ou-mais"
./mvnw test
```

Todo o `domain` é testado com JUnit puro (sem contexto Spring) e os motores de regra usam uma `FonteAleatoria` fake para resultados determinísticos — sem flakiness por causa de `Random` real.

## O que já existe

- Núcleo de combate 1-para-N com vida, atributos dinâmicos e times.
- Fila de turnos por iniciativa, com exclusão automática de combatentes derrotados.
- Dois motores de regra de exemplo (D20 e Percentual), provando o Strategy na prática.
- Casos de uso para iniciar uma partida e processar um ataque.

## Fora de escopo (por enquanto)

- Buffs/debuffs e efeitos ao longo do tempo.
- Persistência (banco de dados).
- Exposição via API REST ou CLI.
- Mecanismos reais de concorrência — o desenho atual já evita estado global mutável, preparando o terreno para isso.


## Stack

Java 17 · Spring Boot 4.1.0 (Maven) · JUnit 5
