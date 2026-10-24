package li1.plp.imperative1.command;

import java.util.LinkedList;
import java.util.List;

import li1.plp.expressions1.util.Tipo;
import li1.plp.expressions2.expression.Expressao;
import li1.plp.expressions2.memory.VariavelJaDeclaradaException;
import li1.plp.expressions2.memory.VariavelNaoDeclaradaException;
import li1.plp.imperative1.memory.AmbienteCompilacaoImperativa;
import li1.plp.imperative1.memory.AmbienteExecucaoImperativa;
import li1.plp.imperative1.memory.ListaValor;
import li1.plp.imperative1.util.Lista;

/**
 * Lista de expressões, usada nos valores do {@code insert}.
 * Segue o formato da ListaExpressao da Imperativa 2.
 */
public class ListaExpressao extends Lista<Expressao> {

    public ListaExpressao() {
    }

    public ListaExpressao(Expressao expressao) {
        super(expressao, new ListaExpressao());
    }

    public ListaExpressao(Expressao expressao, ListaExpressao listaExpressao) {
        super(expressao, listaExpressao);
    }

    /**
     * Avalia cada expressão no ambiente atual, mantendo a ordem.
     */
    public ListaValor avaliar(AmbienteExecucaoImperativa ambiente)
            throws VariavelNaoDeclaradaException, VariavelJaDeclaradaException {
        if (length() >= 2) {
            return new ListaValor(getHead().avaliar(ambiente),
                    ((ListaExpressao) getTail()).avaliar(ambiente));
        } else if (length() == 1) {
            return new ListaValor(getHead().avaliar(ambiente));
        } else {
            return new ListaValor();
        }
    }

    /**
     * Verifica se todas as expressões são bem tipadas.
     */
    public boolean checaTipo(AmbienteCompilacaoImperativa ambiente)
            throws VariavelNaoDeclaradaException, VariavelJaDeclaradaException {
        if (length() == 0) {
            return true;
        }
        return getHead().checaTipo(ambiente)
                && ((ListaExpressao) getTail()).checaTipo(ambiente);
    }

    /**
     * Devolve os tipos das expressões, na ordem em que aparecem.
     */
    public List<Tipo> getTipos(AmbienteCompilacaoImperativa ambiente)
            throws VariavelNaoDeclaradaException, VariavelJaDeclaradaException {
        List<Tipo> tipos = new LinkedList<Tipo>();
        if (length() >= 1) {
            tipos.add(getHead().getTipo(ambiente));
            tipos.addAll(((ListaExpressao) getTail()).getTipos(ambiente));
        }
        return tipos;
    }
}