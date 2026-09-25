package li1.plp.imperative1.command;

import li1.plp.expressions2.expression.Id;
import li1.plp.imperative1.memory.AmbienteCompilacaoImperativa;
import li1.plp.imperative1.memory.AmbienteExecucaoImperativa;
import li1.plp.expressions2.memory.IdentificadorJaDeclaradoException;
import li1.plp.expressions2.memory.IdentificadorNaoDeclaradoException;
import li1.plp.imperative1.util.Lista;

/**
 * Representa o comando {@code create table} da linguagem ImpSQL.
 *
 * O {@code id} representa o nome da tabela que será criada.
 * A {@code colunas} representa a lista de objetos da classe {@link Coluna},
 * sendo cada objeto responsável por representar o nome e o tipo de uma coluna.
 */
public class CreateTable implements Comando{

    private Id id;
    private Lista<Coluna> colunas;

    public CreateTable(Id id, Lista<Coluna> colunas) {
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

        System.out.println("[ImpSQL] Tabela '" + id.getIdName() + "' criada com sucesso na memoria!");
        System.out.println("[ImpSQL] CREATE TABLE " + id + " (" + colunas + ")");
        

        //SqliteManager.getInstance().criarTabela(id, colunas);
            
        return ambiente;
    }

    @Override
    public boolean checaTipo(
            AmbienteCompilacaoImperativa ambiente)
            throws IdentificadorJaDeclaradoException,
            IdentificadorNaoDeclaradoException {

        return true;
    }

    @Override
    public String toString() {
        return "CREATE TABLE " + id + " (" + colunas + ")";
}
    
}