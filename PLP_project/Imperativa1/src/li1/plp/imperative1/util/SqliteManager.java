package li1.plp.imperative1.util;

import li1.plp.expressions2.expression.Id;
import li1.plp.imperative1.command.Coluna;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

/**
 * Gerencia conexões e operações no banco SQLite.
 */
public class SqliteManager {

    private static final String URL_BANCO = "jdbc:sqlite:banco_impSQL.db";

    private static SqliteManager instance;

    private SqliteManager() {}

    public static synchronized SqliteManager getInstance() {
        if (instance == null) {
            instance = new SqliteManager();
        }
        return instance;
    }

    /**
     * Cria uma tabela no banco SQLite a partir das colunas da ImpSQL.
     */
    public void criarTabela(Id id, Lista<Coluna> colunas) {

        StringBuilder sql = new StringBuilder("CREATE TABLE IF NOT EXISTS ");
        sql.append(id.getIdName()).append(" (");

        Lista<Coluna> aux = colunas;

        while (aux != null && aux.getHead() != null) {

            Coluna col = aux.getHead();

            sql.append(col.getId().getIdName())
               .append(" ")
               .append(SqliteHelper.converterParaTipoSql(col.getTipo()));

            aux = aux.getTail();

            if (aux != null && aux.getHead() != null) {
                sql.append(", ");
            }
        }

        sql.append(");");

        try (Connection conn = DriverManager.getConnection(URL_BANCO);
             Statement stmt = conn.createStatement()) {

            stmt.executeUpdate(sql.toString());

            System.out.println("[ImpSQL] Tabela '" + id.getIdName()
                    + "' criada no SQLite.");

        } catch (Exception e) {
            System.err.println(
                "Erro ao criar tabela no SQLite: " + e.getMessage()
            );
        }
    }
}