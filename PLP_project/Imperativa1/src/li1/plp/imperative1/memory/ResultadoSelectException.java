package li1.plp.imperative1.memory;

/**
 * Exceção lançada quando um select com into não retorna exatamente
 * um registro.
 */
public class ResultadoSelectException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public ResultadoSelectException(String mensagem) {
        super(mensagem);
    }
}