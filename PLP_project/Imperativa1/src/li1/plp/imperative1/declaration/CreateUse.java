package li1.plp.imperative1.declaration;

import li1.plp.expressions2.expression.Id;
import li1.plp.imperative1.memory.AmbienteCompilacaoImperativa;
import li1.plp.imperative1.memory.AmbienteExecucaoImperativa;
import li1.plp.expressions2.memory.IdentificadorJaDeclaradoException;
import li1.plp.expressions2.memory.IdentificadorNaoDeclaradoException;
import li1.plp.imperative1.util.Lista;
import li1.plp.imperative1.util.SqliteManager;
import li1.plp.imperative1.util.Coluna;
import java.util.HashSet;

/**
 * Representa a definicao {@code create-use} da linguagem ImpSQL.
 *
 * Cria a tabela no banco caso ela ainda não exista; se já existir,
 * a tabela existente é usada.
 *
 * O {@code id} representa o nome da tabela.
 * A {@code colunas} representa a lista de objetos da classe {@link Coluna},
 * sendo cada objeto responsável por representar o nome e o tipo de uma coluna.
 */
public class CreateUse{

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

    
    public AmbienteExecucaoImperativa elabora(
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
    /**
     * Verifica se o esquema declarado é igual ao esquema da tabela no banco:
     * mesmas colunas, na mesma ordem, com os mesmos tipos.
     */
    private boolean mesmoEsquema(Lista<Coluna> outroEsquema) {
        Lista<Coluna> declarada = colunas;
        Lista<Coluna> banco = outroEsquema;

        while (declarada != null && declarada.getHead() != null
                && banco != null && banco.getHead() != null) {

            Coluna colunaDeclarada = declarada.getHead();
            Coluna colunaBanco = banco.getHead();

            if (!colunaDeclarada.getId().equals(colunaBanco.getId())) {
                return false;       // nome diferente nessa posição
            }

            // Um tipo null vem de uma coluna com tipo que a ImpSQL não conhece.
            if (colunaBanco.getTipo() == null
                    || !colunaDeclarada.getTipo().eIgual(colunaBanco.getTipo())) {
                return false;       // tipo diferente nessa posição
            }

            declarada = declarada.getTail();
            banco = banco.getTail();
        }

        // As duas listas precisam terminar juntas: mesmo número de colunas.
        boolean fimDeclarada = declarada == null || declarada.getHead() == null;
        boolean fimBanco = banco == null || banco.getHead() == null;
        return fimDeclarada && fimBanco;
    }

    
    /**
     * Verifica se o create-use é bem tipado: 
     * o esquema é igual ao de uma definição anterior da mesma tabela, se houver; 
     * os nomes das colunas são distintos; 
     * e o esquema é igual ao da tabela no banco, se ela já existir. Os tipos das colunas são válidos pela gramática.
     *
     * Se for bem tipado, registra a tabela no mapa de tabelas.
     */
    public boolean checaTipo(AmbienteCompilacaoImperativa ambiente)
            throws IdentificadorJaDeclaradoException,
            IdentificadorNaoDeclaradoException {

        boolean resposta = compativelComDefinicaoAnterior(ambiente) // igual à definição anterior
                && colunasDistintas()                               // sem colunas repetidas
                && compativelComBanco();                            // igual à tabela no banco

        // Só registra a tabela depois que a verificação passa.
        if (resposta) {
            ambiente.mapTabela(id, colunas);
        }
        return resposta;
    }

    /**
     * Verifica se o esquema é igual ao de uma definição anterior da mesma
     * tabela, na mesma lista de create-use. Sem definição anterior, o
     * resultado é true.
     */
    private boolean compativelComDefinicaoAnterior(AmbienteCompilacaoImperativa ambiente) {
        return !ambiente.existeTabela(id) || mesmoEsquema(ambiente.getTabela(id));
    }

    /**
     * Verifica se o esquema é igual ao da tabela no banco. Se a tabela
     * ainda não existe no banco, o resultado é true.
     */
    private boolean compativelComBanco() {
        Lista<Coluna> esquemaBanco = SqliteManager.getInstance().lerEsquema(id);
        return esquemaBanco == null || mesmoEsquema(esquemaBanco);
    }

    @Override
    public String toString() {
        return "CREATE-USE " + id + " (" + colunas + ")";
    }
    
}