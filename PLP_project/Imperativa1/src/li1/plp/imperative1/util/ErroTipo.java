package li1.plp.imperative1.util;

/**
 * Mensagens de erro de tipo da ImpSQL.
 */
public class ErroTipo {

    /**
     * Informa no terminal o motivo do erro de tipo e devolve false,
     * para ser usado no lugar de um return false no checaTipo.
     */
    public static boolean erro(String motivo) {
        System.err.println("[ImpSQL] Erro de tipo: " + motivo);
        return false;
    }
}