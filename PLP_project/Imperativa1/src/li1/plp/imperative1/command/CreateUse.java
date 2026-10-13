package li1.plp.imperative1.command;

import li1.plp.expressions2.expression.Id;
import li1.plp.imperative1.memory.AmbienteCompilacaoImperativa;
import li1.plp.imperative1.memory.AmbienteExecucaoImperativa;
import li1.plp.expressions2.memory.IdentificadorJaDeclaradoException;
import li1.plp.expressions2.memory.IdentificadorNaoDeclaradoException;
import li1.plp.imperative1.util.Lista;
import li1.plp.imperative1.util.SqliteManager;
import java.util.HashSet;

/**
 * Representa o comando {@code create-use} da linguagem ImpSQL.
 *
 * Cria a tabela no banco caso ela ainda não exista; se já existir,
 * a tabela existente é usada.
 *
 * O {@code id} representa o nome da tabela.
 * A {@code colunas} representa a lista de objetos da classe {@link Coluna},
 * sendo cada objeto responsável por representar o nome e o tipo de uma coluna.
 */
public class CreateUse implements Comando{

    private Id id;
    private Lista<Coluna> colunas;

    public CreateUse(Id id, Lista<Coluna> colunas) {
        this.id = id;
        this.colunas = colunas;
    }

    public Id getId() {
        return id;
    }

    public Lista<Coluna> getColunas() {
        return colunas;
    }

     @Override
    public AmbienteExecucaoImperativa executar(
            AmbienteExecucaoImperativa ambiente)
            throws IdentificadorJaDeclaradoException,
            IdentificadorNaoDeclaradoException {

         //System.out.println("[ImpSQL] Tabela '" + id.getIdName() + "' criada com sucesso na memoria!");
         //System.out.println("[ImpSQL] CREATE TABLE " + id + " (" + colunas + ")");
        

        SqliteManager.getInstance().criarTabela(id, colunas);
            
        return ambiente;
    }
        /**
     * Verifica se os nomes das colunas são distintos entre si.
     */
    private boolean colunasDistintas() {
        HashSet<Id> nomes = new HashSet<Id>();

        Lista<Coluna> aux = colunas;
        while (aux != null && aux.getHead() != null) {
            Id nome = aux.getHead().getId();
            if (!nomes.add(nome)) {
                return false;       // nome repetido
            }
            aux = aux.getTail();
        }
        return true;
    }

    @Override
    public boolean checaTipo(
            AmbienteCompilacaoImperativa ambiente)
            throws IdentificadorJaDeclaradoException,
            IdentificadorNaoDeclaradoException {

       


        // Regra 1: a tabela não pode ter sido definida antes na mesma lista.
        if (ambiente.existeTabela(id)) {
            return false;
        }

        // Regra 2: os nomes das colunas devem ser distintos.
        if (!colunasDistintas()) {
            return false;
        }

        // Ainda falta: comparação com o esquema do banco.

        ambiente.mapTabela(id, colunas);
        return true;
    }

    @Override
    public String toString() {
        return "CREATE-USE " + id + " (" + colunas + ")";
    }
    
}