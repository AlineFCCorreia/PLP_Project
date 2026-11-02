package li1.plp.imperative1.memory;

import li1.plp.expressions2.expression.Id;
import li1.plp.expressions2.memory.IdentificadorNaoDeclaradoException;

/**
 * Exceção lançada quando uma tabela é usada sem ter sido definida
 * por um create-use no início do programa.
 */
public class TabelaNaoDeclaradaException extends IdentificadorNaoDeclaradoException {

    private static final long serialVersionUID = 1L;

    public TabelaNaoDeclaradaException(Id id) {
        super("Tabela " + id + " nao definida por um create-use neste programa.");
    }
}
