package li1.plp.imperative1.command;

import li1.plp.expressions2.expression.Id;
import li1.plp.expressions2.memory.IdentificadorJaDeclaradoException;
import li1.plp.expressions2.memory.IdentificadorNaoDeclaradoException;
import li1.plp.imperative1.memory.AmbienteCompilacaoImperativa;
import li1.plp.imperative1.memory.AmbienteExecucaoImperativa;
import li1.plp.imperative1.memory.EntradaVaziaException;
import li1.plp.imperative1.memory.ErroTipoEntradaException;
import li1.plp.imperative1.util.Lista;
import li1.plp.imperative1.util.SqliteManager;
import java.util.List;
import li1.plp.imperative1.util.Coluna;
import li1.plp.imperative1.util.ErroTipo;

import li1.plp.expressions1.util.Tipo;
import li1.plp.imperative1.memory.ListaValor;

/**
 * Representa o comando {@code insert into T values (e1, ..., en)} da ImpSQL.
 */
public class Insert implements Comando {

    private Id id;
    private ListaExpressao expressoes;

    public Insert(Id id, ListaExpressao expressoes) {
        this.id = id;
        this.expressoes = expressoes;
    }

    /**
     * Avalia as expressões no ambiente atual e grava os valores na tabela.
     */
    @Override
    public AmbienteExecucaoImperativa executar(AmbienteExecucaoImperativa ambiente)
            throws IdentificadorJaDeclaradoException, IdentificadorNaoDeclaradoException,
            EntradaVaziaException, ErroTipoEntradaException {

        ListaValor valores = expressoes.avaliar(ambiente);
        SqliteManager.getInstance().inserir(id, valores);
        return ambiente;
    }

    /**
     * Verifica se o insert é bem tipado: a tabela foi definida por um
     * create-use, as expressões são bem tipadas e os tipos dos valores
     * correspondem aos das colunas, posição a posição.
     */
    public boolean checaTipo(AmbienteCompilacaoImperativa ambiente)
            throws IdentificadorJaDeclaradoException, IdentificadorNaoDeclaradoException,
            EntradaVaziaException {

        // Esquema da tabela
        Lista<Coluna> colunas = ambiente.getTabela(id);

        return colunas != null                                               // a tabela foi definida, nunca vem null
                && expressoes.checaTipo(ambiente)                            // as expressões são bem tipadas
                && tiposCompativeis(expressoes.getTipos(ambiente), colunas); // os tipos batem com as colunas
    }

    /**
     * Verifica se os tipos dos valores correspondem aos tipos das colunas:
     * mesma quantidade e mesmo tipo em cada posição.
     */
    private boolean tiposCompativeis(List<Tipo> tipos, Lista<Coluna> colunas) {
        // Um valor para cada coluna.
        if (tipos.size() != colunas.length()) {
            return ErroTipo.erro("a tabela " + id + " tem " + colunas.length()
                    + " colunas, mas foram dados " + tipos.size() + " valores");
        }

        // O i-esimo valor tem o tipo da i-ésima coluna.
        Lista<Coluna> coluna = colunas;
        for (Tipo tipo : tipos) {
            if (!tipo.eIgual(coluna.getHead().getTipo())) {
                return ErroTipo.erro("o valor da coluna " + coluna.getHead().getId()
                        + " deveria ser " + coluna.getHead().getTipo() + ", mas e " + tipo);
            }
            coluna = coluna.getTail();
        }
        return true;
    }

    @Override
    public String toString() {
        return "INSERT INTO " + id + " VALUES (" + expressoes + ")";
    }
}
