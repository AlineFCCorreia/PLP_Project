# ImpSQL — Extensão da Linguagem Imperativa 1 para Manipulação e Consulta de Dados

## Universidade Federal de Pernambuco

**Centro de Informática**  
**Disciplina:** IN1007-2026.2 - **Paradigmas de Linguagens de Programação**

## Equipe

- **Aline Franciele Correia da Silva** - <afcs@cin.ufpe.br>
- **Pedro Mesquita Breasil** - <pmb2@cin.ufpe.br>

Este repositório organiza o projeto da disciplina de Paradigmas de Linguagens de Programação com o código-fonte do fork organizado em `PLP_project/`.

## Introdução

O projeto consiste na extensão da linguagem Imperativa 1 com uma pequena linguagem de comandos inspirados em SQL para manipulação de dados estruturados. A extensão permite criar tabelas, inserir registros e realizar consultas sobre os dados armazenados. 

## Objetivos

### Objetivo Geral

Estender a linguagem Imperativa 1 com comandos para criação, inserção e consulta de dados estruturados, incluindo seleção e filtragem de registros.


### Objetivos Específicos

- Criar tabelas com colunas e tipos de dados definidos.
- Inserir registros nas tabelas.
- Consultar dados armazenados nas tabelas.
- Selecionar colunas específicas nas consultas.
- Filtrar registros utilizando condições baseadas nas expressões da linguagem.

## Estrutura do Repositório

- `PLP_project/`: código-fonte do fork com os módulos e a documentação técnica original.
- `README.md`: visão geral do projeto, proposta e BNF.
- `Documents/`: espaço reservado para documentação e apresentações.

## Resumo do Projeto

A extensão adiciona três novos tipos de comandos à linguagem Imperativa 1:

- `Create` —  criação da estrutura de uma tabela.
- `Insert into` — inserção de um registro na tabela.
- `Select [... where Expressao]` — consulta de dados, com filtragem opcional.


### Possíveis extensões futuras

- `Update` — atualização de registros existentes.
- `Delete` — remoção de registros.

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

Create ::= "create" Id "(" ListaColunas ")"

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