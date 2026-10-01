# ImpSQL — Extensão da Linguagem Imperativa 1 para Manipulação e Consulta de Dados

## Universidade Federal de Pernambuco

**Centro de Informática**  
**Disciplina:** IN1007-2026.2 - **Paradigmas de Linguagens de Programação**

## Equipe

- **Aline Franciele Correia da Silva** - <afcs@cin.ufpe.br>
- **Pedro Mesquita Brasil** - <pmb2@cin.ufpe.br>

Este repositório organiza o projeto da disciplina de Paradigmas de Linguagens de Programação com o código-fonte do fork organizado em `PLP_project/`.

## Introdução

O projeto consiste na extensão da linguagem Imperativa 1 com uma pequena linguagem de comandos inspirados em SQL para manipulação de dados estruturados. A extensão foi projetada para permitir a criação de tabelas, inserção de registros e realização de consultas sobre os dados armazenados. Os dados são persistidos em um banco de dados SQLite, acessado por uma camada de persistência própria.

## Objetivos

### Objetivo Geral

Estender a linguagem Imperativa 1 com comandos para criação, inserção e consulta de dados estruturados, incluindo seleção e filtragem de registros.


### Objetivos Específicos

- Criar tabelas com colunas e tipos de dados definidos.
- Inserir registros nas tabelas.
- Consultar dados armazenados nas tabelas.
- Selecionar colunas específicas nas consultas.
- Filtrar registros utilizando condições baseadas nas expressões da linguagem.
- Persistir os dados em um banco de dados SQLite.

## Estrutura do Repositório

- `PLP_project/`: código-fonte do fork com os módulos e a documentação técnica original.
- `README.md`: visão geral do projeto, proposta, BNF e especificação semântica.
- `Documents/`: espaço reservado para documentação e apresentações.

## Resumo do Projeto

A extensão adiciona três novos tipos de comandos à linguagem Imperativa 1:

- `create table` —  criação da estrutura de uma tabela.
- `insert into` — inserção de um registro na tabela.
- `select [... where Expressao]` — consulta de dados, com filtragem opcional.


## Exemplos de Uso

A seguir são apresentados exemplos simples dos novos comandos adicionados à linguagem.

### Criar uma tabela
O comando `create table` permite criar uma tabela informando suas colunas e respectivos tipos:

```text
create table clientes (
    nome string,
    idade int,
    ativo boolean
)
```


### Inserir um registro

O comando `insert into` permite inserir valores, literais ou expressões, em uma tabela:

```text
{ var n = "Bob", var i = 30;
  insert into clientes values ("Alice", 25, true);
  insert into clientes values (n, i + 1, false)
}
```

### Consultar todos os dados

O comando `select` permite consultar todos os registros e colunas de uma tabela:

```text
select * from clientes
```

### Consultar colunas específicas

Também é possível selecionar apenas algumas colunas:

```text
select nome from clientes
```

### Filtrar registros

O comando `select` pode utilizar uma condição para filtrar os registros:

```text
select nome from clientes where idade == 25
```


## BNF

```
Programa ::= Comando

Comando ::= Atribuicao
       | ComandoDeclaracao
       | While
       | IfThenElse
       | IO
       | Comando “;” Comando
       | Skip
       | Create     [NOVO]
       | Insert     [NOVO]
       | Select     [NOVO]

Skip ::=

Atribuicao ::= Id “:=” Expressao

Expressao ::= Valor
       | ExpUnaria
       | ExpBinaria
       | Id

Valor ::= ValorConcreto

ValorConcreto ::= ValorInteiro
       | ValorBooleano
       | ValorString

ExpUnaria ::= “-“ Expressao
       | “not” Expressao
       | “length” Expressao

ExpBinaria ::= Expressao “+” Expressao
       | Expressao “-“ Expressao
       | Expressao “and” Expressao
       | Expressao “or” Expressao
       | Expressao “==” Expressao
       | Expressao “++” Expressao

ComandoDeclaracao ::= “{“ Declaracao “;” Comando “}”

Declaracao ::= DeclaracaoVariavel
       | DeclaracaoComposta

DeclaracaoVariavel ::= “var” Id “=” Expressao

DeclaracaoComposta ::= Declaracao “,” Declaracao

While ::= “while” Expressao “do” Comando

IfThenElse ::= “if” Expressao “then” Comando “else” Comando

IO ::= “write” “(“ Expressao “)”
       | “read” “(“ Id “)”

;-------------------------------------------------------
; Comandos novos
;-------------------------------------------------------

Create ::= "create table" Id "(" ListaColunas ")"

Insert ::= "insert" "into" Id
           "values" "(" ListaExpressao ")"

Select ::= "select" "*" "from" Id
         | "select" "*" "from" Id "where" Expressao
         | "select" ListaId "from" Id
         | "select" ListaId "from" Id "where" Expressao

;-------------------------------------------------------
; Definições auxiliares
;-------------------------------------------------------

ListaColunas ::= Coluna
        | Coluna "," ListaColunas

Coluna ::= Id Tipo

Tipo ::= "int"
       | "boolean"
       | "string"

ListaExpressao ::= Expressao
                  | Expressao "," ListaExpressao

ListaId ::= Id
          | Id "," ListaId

```


## Decisões de Projeto

### Representação das tabelas no ambiente de compilação

O esquema de cada tabela (nome, colunas e tipos) é registrado em um **mapa de tabelas** no contexto de compilação, separado do mapeamento de variáveis. É esse mapa que permite ao `insert` e ao `select` verificar tipos estaticamente.

- **Espaço de nomes separado:** tabelas e variáveis não compartilham nomes. `var clientes = 1` e `create table clientes (...)` podem coexistir.
- **Escopo global:** o mapa de tabelas não faz parte da pilha de escopos das variáveis. Uma tabela declarada com `create table` é visível do ponto da declaração até o fim do programa, independentemente dos blocos `{ ... }`. Isso mantém o ambiente de compilação coerente com o banco, onde a tabela continua existindo após o fim do bloco.

### Persistência entre execuções

No início de cada execução, antes da verificação de tipos, o esquema das tabelas já existentes no SQLite é carregado no mapa de tabelas. Assim, o ambiente de compilação reflete o que está persistido, e um programa pode consultar tabelas criadas em execuções anteriores. Como consequência, executar novamente um `create table` de uma tabela já existente é erro de tipo.

### Mapeamento de tipos para o SQLite

| ImpSQL    | SQLite    | Valor armazenado |
|-----------|-----------|------------------|
| `int`     | `INTEGER` | inteiro          |
| `string`  | `TEXT`    | texto            |
| `boolean` | `BOOLEAN` | `0` ou `1`       |

O SQLite não possui uma classe de armazenamento específica para valores booleanos. Entretanto permite declarar uma coluna como `BOOLEAN`, mantendo esse tipo declarado no esquema da tabela. Os valores booleanos são armazenados como inteiros, com `false` representado por `0` e `true` por `1`. Isso permite recuperar o tipo `boolean` ao carregar o esquema das tabelas persistidas.

A conversão entre `ValorBooleano` e `0`/`1` é realizada na camada de
persistência, enquanto a linguagem continua tratando esses valores como
booleanos.

### Avaliação do `where`

A condição do `where` é avaliada pelo interpretador da Imp1, e não traduzida para SQL. Os registros são lidos do banco e, para cada um, a expressão é avaliada em um ambiente em que as colunas estão associadas aos valores do registro. Assim, o filtro segue a semântica da ImpSQL.

### Ausência de valores nulos

A linguagem não possui `null`: todo registro tem um valor para cada coluna.

## Semântica Estática (`checaTipo`)

Os erros de esquema são detectados na verificação de tipos, antes da execução.

### `create table T (c1 t1, ..., cn tn)`

É bem tipado se:

1. A tabela `T` não existe no mapa de tabelas.
2. Os nomes `c1, ..., cn` são distintos entre si.
3. Os tipos `t1, ..., tn` são válidos, o que já é garantido pela gramática.

Efeito: registra `T ↦ (c1 t1, ..., cn tn)` no mapa de tabelas.

### `insert into T values (e1, ..., em)`

É bem tipado se:

1. A tabela `T` existe no mapa de tabelas.
2. O número de expressões é igual ao número de colunas de `T` (se `T` tem `n` colunas, `m = n`).
3. Cada `ei` é bem tipada, e seu tipo é igual ao da i-ésima coluna (correspondência posicional).

### `select L from T [where e]`

É bem tipado se:

1. A tabela `T` existe no mapa de tabelas.
2. Se `L` é uma lista de identificadores, cada um é uma coluna de `T`. Se `L` é `*`, todas as colunas são selecionadas.
3. Se houver `where e`: em um escopo temporário em que cada coluna de `T` é mapeada para seu tipo, `e` é bem tipada e tem tipo `boolean`. Nesse escopo, uma coluna esconde uma variável de mesmo nome (sombreamento). O escopo é descartado ao fim da verificação.

## Semântica Dinâmica (`executar`)

### `create table`

Cria a tabela no SQLite com as colunas e os tipos declarados, conforme o mapeamento de tipos.

### `insert into`

Avalia as expressões `e1, ..., em` no ambiente de execução atual e grava um novo registro na tabela, convertendo cada valor para o tipo correspondente do SQLite. As expressões são avaliadas no momento da inserção, e apenas os valores resultantes são armazenados.

### `select`

1. Lê os registros da tabela, convertendo cada valor de volta para `ValorInteiro`, `ValorString` ou `ValorBooleano`, de acordo com o tipo da coluna.
2. Se houver `where e`, avalia `e` para cada registro, em um escopo temporário em que cada coluna está associada ao seu valor, e mantém os registros em que o resultado é `true`.
3. Escreve na saída cada valor selecionado, como um valor da linguagem, da mesma forma que o comando `write`: na ordem dos registros e, dentro de cada registro, na ordem das colunas pedidas. Se nenhum registro for selecionado, nada é escrito.

   Exemplo: após os `insert` da seção de exemplos, o comando `select * from clientes` produz a saída `"Alice" 25 true "Bob" 31 false`, e `select nome from clientes where idade == 25` produz `"Alice"`.

O `select` não produz um valor utilizável pelo programa: o resultado vai apenas para a saída, pois ainda não há um tipo capaz de representar uma coleção de registros.

### Erros de execução

Falhas do banco (arquivo inacessível, erro de SQL) interrompem a execução com uma exceção. O banco fica no arquivo `banco_impSQL.db`, no diretório em que o interpretador é executado.

## Limitações e Extensões Futuras

- `update`: atualização de registros existentes.
- `delete`: remoção de registros.
- `drop table`: remoção de tabelas.
- Iteração sobre o resultado de uma consulta (`for each ... in T where e do Comando`): o corpo é executado uma vez para cada registro, com as colunas visíveis como variáveis. Isso permite operar sobre os registros com os comandos da linguagem sem introduzir novos tipos, reaproveitando o escopo temporário usado na avaliação do `where`.
- Valores nulos.

## Status da Implementação

As seções de especificação acima descrevem a linguagem ImpSQL como definida pela equipe. A implementação está em andamento:

- [x] `create table`: gramática, parser e criação da tabela no SQLite
- [ ] `create table`: verificação de tipos e registro no mapa de tabelas
- [ ] Carregamento do esquema das tabelas persistidas
- [ ] `insert into`
- [ ] `select`
- [ ] Tratamento de erros do banco com exceções

