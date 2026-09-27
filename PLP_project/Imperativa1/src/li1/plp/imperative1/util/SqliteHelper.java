package li1.plp.imperative1.util;

import li1.plp.expressions1.util.Tipo;

/**
 * Funções auxiliares para integração da ImpSQL com o SQLite.
 */
public class SqliteHelper {

    /**
     * Converte um Tipo da ImpSQL para o tipo correspondente no SQLite.
     */
    public static String converterParaTipoSql(Tipo tipo) {
        String nomeTipo = tipo.getNome().toLowerCase();

        if (nomeTipo.contains("int")) {
            return "INTEGER";
        } else if (nomeTipo.contains("bool")) {
            return "BOOLEAN";
        } else if (nomeTipo.contains("string")) {
            return "TEXT";
        }

        throw new IllegalArgumentException(
            "Tipo não suportado: " + tipo.getNome()
        );
    }
}