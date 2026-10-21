package li1.plp.imperative1.util;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import li1.plp.expressions2.expression.Valor;
import li1.plp.expressions2.expression.ValorBooleano;
import li1.plp.expressions2.expression.ValorInteiro;
import li1.plp.expressions2.expression.ValorString;
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


    /**
     * Coloca um valor da ImpSQL na posição indicada de um comando SQL preparado.
     * A posição começa em 1, como no JDBC.
     */
    public static void definirParametro(PreparedStatement stmt, int posicao, Valor valor)
            throws SQLException {

        if (valor instanceof ValorInteiro) {
            stmt.setInt(posicao, ((ValorInteiro) valor).valor());
        } else if (valor instanceof ValorString) {
            stmt.setString(posicao, ((ValorString) valor).valor());
        } else if (valor instanceof ValorBooleano) {
            stmt.setBoolean(posicao, ((ValorBooleano) valor).valor());
        } else {
            throw new IllegalArgumentException("Valor não suportado: " + valor);
        }
    }
}