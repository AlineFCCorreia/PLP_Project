package li1.plp.imperative1.util;

import li1.plp.expressions1.util.Tipo;
import li1.plp.expressions1.util.TipoPrimitivo;

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

        /**
     * Converte um tipo declarado no SQLite para o Tipo correspondente da ImpSQL.
     * Devolve null se o tipo não for um dos usados pela ImpSQL, por exemplo
     * numa tabela criada fora da linguagem.
     */
    public static Tipo converterParaTipoImpSQL(String tipoSql) {
        switch (tipoSql.toUpperCase()) {
            case "INTEGER":
                return TipoPrimitivo.INTEIRO;
            case "TEXT":
                return TipoPrimitivo.STRING;
            case "BOOLEAN":
                return TipoPrimitivo.BOOLEANO;
            default:
                return null;
        }
    }
}