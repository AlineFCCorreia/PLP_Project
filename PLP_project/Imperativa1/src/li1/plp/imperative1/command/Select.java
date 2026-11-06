package li1.plp.imperative1.command;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import li1.plp.expressions1.util.Tipo;
import li1.plp.expressions2.expression.Expressao;
import li1.plp.expressions2.expression.Id;
import li1.plp.expressions2.expression.Valor;
import li1.plp.expressions2.expression.ValorBooleano;
import li1.plp.expressions2.memory.IdentificadorJaDeclaradoException;
import li1.plp.expressions2.memory.IdentificadorNaoDeclaradoException;
import li1.plp.imperative1.memory.AmbienteCompilacaoImperativa;
import li1.plp.imperative1.memory.AmbienteExecucaoImperativa;
import li1.plp.imperative1.memory.EntradaVaziaException;
import li1.plp.imperative1.memory.ErroTipoEntradaException;
import li1.plp.imperative1.memory.ResultadoSelectException;
import li1.plp.imperative1.util.Lista;
import li1.plp.imperative1.util.Coluna;
import li1.plp.imperative1.util.ErroTipo;
import li1.plp.imperative1.util.SqliteManager;



/**
 * Representa o comando {@code select L [into V] from T [where e] [do C]}
 * da ImpSQL. {@code colunas == null} representa o {@code *},
 * {@code variaveis == null} representa a ausência de {@code into},
 * {@code condicao == null} representa a ausência de {@code where}, e
 * {@code comando == null} representa a ausência de {@code do}.
 */
public class Select implements Comando {

    private Id id;
    private Lista<Id> colunas;
    private Expressao condicao;
    private Lista<Id> variaveis;   
    private Comando comando;

    public Select(Id id, Lista<Id> colunas, Lista<Id> variaveis, Expressao condicao, Comando comando) {
        this.id = id;
        this.colunas = colunas;
        this.variaveis = variaveis;
        this.condicao = condicao;
        this.comando = comando;
    }

    /**
     * Lê os registros da tabela e filtra com o where. Sem into, escreve na
     * saída os valores das colunas selecionadas. Com into e do, para cada
     * registro selecionado, atribui os valores às variáveis e executa o
     * comando. Com into e sem do, atribui às variáveis os valores do único
     * registro selecionado; se nenhum ou mais de um registro for
     * selecionado, lança ResultadoSelectException.
     */
    @Override
    public AmbienteExecucaoImperativa executar(AmbienteExecucaoImperativa ambiente)
            throws IdentificadorJaDeclaradoException, IdentificadorNaoDeclaradoException,
            EntradaVaziaException, ErroTipoEntradaException {

        List<LinkedHashMap<Id, Valor>> registros = SqliteManager.getInstance().lerRegistros(id);
        List<LinkedHashMap<Id, Valor>> selecionados = new ArrayList<LinkedHashMap<Id, Valor>>();

        for (LinkedHashMap<Id, Valor> registro : registros) {
            if (condicao == null || satisfazCondicao(registro, ambiente)) {
                selecionados.add(registro);
            }
        }

        if (variaveis == null) {
            for (LinkedHashMap<Id, Valor> registro: selecionados) {
                escreverRegistro(registro, ambiente);
            }  
        } else if (comando != null){
            for (LinkedHashMap<Id, Valor> registro: selecionados) {
                atribuirRegistro(registro, ambiente);
                comando.executar(ambiente);
            }
        } else {
            if (selecionados.size() != 1) {
                throw new ResultadoSelectException(selecionados.isEmpty()
                        ? "o select nao retornou nenhum registro"
                        : "o select retornou mais de um registro");
            }
            atribuirRegistro(selecionados.get(0), ambiente);
        }
        return ambiente;
    }

    /**
     * Avalia o where num escopo temporário em que cada coluna está
     * associada ao seu valor no registro.
     */
    private boolean satisfazCondicao(LinkedHashMap<Id, Valor> registro,
            AmbienteExecucaoImperativa ambiente)
            throws IdentificadorJaDeclaradoException, IdentificadorNaoDeclaradoException {

        ambiente.incrementa();
        try {
            for (Map.Entry<Id, Valor> coluna : registro.entrySet()) {
                ambiente.map(coluna.getKey(), coluna.getValue());
            }
            return ((ValorBooleano) condicao.avaliar(ambiente)).valor();
        } finally {
            ambiente.restaura();
        }
    }

    /**
     * Escreve na saída os valores das colunas selecionadas:
     * todas, na ordem da tabela, ou as pedidas, na ordem do select.
     */
    private void escreverRegistro(LinkedHashMap<Id, Valor> registro,
            AmbienteExecucaoImperativa ambiente) {

        if (colunas == null) {
            for (Valor valor : registro.values()) {
                ambiente.write(valor);
            }
        } else {
            Lista<Id> aux = colunas;
            while (aux != null && aux.getHead() != null) {
                ambiente.write(registro.get(aux.getHead()));
                aux = aux.getTail();
            }
        }
    }

    /**
     * Atribui às variáveis do into os valores das colunas selecionadas,
     * na ordem do select.
     */
    private void atribuirRegistro(LinkedHashMap<Id, Valor> registro,
            AmbienteExecucaoImperativa ambiente)
            throws IdentificadorNaoDeclaradoException {

        Lista<Id> variavel = variaveis;
        Lista<Id> coluna = colunas;
        Iterator<Valor> valoresTabela = registro.values().iterator();

        while (variavel != null && variavel.getHead() != null) {
            Valor valor;
            if (colunas == null) {
                valor = valoresTabela.next();               // select *: ordem da tabela
            } else {
                valor = registro.get(coluna.getHead());     // ordem do select
                coluna = coluna.getTail();
            }
            ambiente.changeValor(variavel.getHead(), valor);
            variavel = variavel.getTail();
        }
    }

    /**
     * Verifica se o select é bem tipado: a tabela foi definida por um
     * create-use, as colunas pedidas existem na tabela, se houver where,
     * a condição é bem tipada e booleana, se houver into, as variáveis
     * foram declaradas, são tantas quanto as colunas e têm os tipos delas,
     * e, se houver do, o comando é bem tipado.
     */
    @Override
    public boolean checaTipo(AmbienteCompilacaoImperativa ambiente)
            throws IdentificadorJaDeclaradoException, IdentificadorNaoDeclaradoException,
            EntradaVaziaException {

        
        Lista<Coluna> esquema = ambiente.getTabela(id); // Esquema da tabela

        return esquema != null                                       // a tabela foi definida, nunca vem null
                && colunasExistem(esquema)                           // as colunas pedidas existem
                && condicaoBemTipada(ambiente, esquema)              // o where é booleano
                && variaveisCompativeis(ambiente, esquema)           // o into combina com as colunas
                && (comando == null || comando.checaTipo(ambiente)); // o corpo do do é bem tipado.
    } 
    /**
     * Verifica se cada coluna pedida existe na tabela.
     * No select *, não há colunas pedidas, e o resultado é true.
     */
    private boolean colunasExistem(Lista<Coluna> esquema) {
        Lista<Id> aux = colunas;
        while (aux != null && aux.getHead() != null) {
            if (!existeColuna(esquema, aux.getHead())) {
                return ErroTipo.erro(aux.getHead() + " nao e uma coluna da tabela " + id);
            }
            aux = aux.getTail();
        }
        return true;
    }

    /**
     * Verifica o where num escopo temporário em que cada coluna da tabela
     * está associada ao seu tipo. Uma coluna esconde uma variável de mesmo
     * nome. Sem where, o resultado é true.
     */
    private boolean condicaoBemTipada(AmbienteCompilacaoImperativa ambiente,
            Lista<Coluna> esquema)
            throws IdentificadorJaDeclaradoException, IdentificadorNaoDeclaradoException {

        if (condicao == null) {
            return true;
        }

        boolean resposta;
        ambiente.incrementa();

        // As colunas entram no escopo como variáveis, com o tipo de cada uma.
        Lista<Coluna> coluna = esquema;
        while (coluna != null && coluna.getHead() != null) {
            ambiente.map(coluna.getHead().getId(), coluna.getHead().getTipo());
            coluna = coluna.getTail();
        }

        resposta = condicao.checaTipo(ambiente)
                && (condicao.getTipo(ambiente).eBooleano()
                    || ErroTipo.erro("a condicao do where nao e booleana"));
        ambiente.restaura();
        return resposta;
    }

    /**
     * Indica se a coluna faz parte do esquema da tabela.
     */
    private boolean existeColuna(Lista<Coluna> esquema, Id nome) {
        return buscarColuna(esquema, nome) != null;
    }

    /**
     * Verifica se cada variável do into foi declarada e tem o tipo da
     * coluna correspondente, e se há tantas variáveis quanto colunas.
     * A verificação é feita fora do escopo do where, para que uma coluna
     * não esconda uma variável de mesmo nome. Sem into, o resultado é true.
     */
    private boolean variaveisCompativeis(AmbienteCompilacaoImperativa ambiente, Lista<Coluna> esquema) 
            throws IdentificadorNaoDeclaradoException{

        if (variaveis == null) {
            return true;
        }

        Lista<Id> variavel = variaveis;
        Lista<Coluna> coluna = colunasSelecionadas(esquema);
         while (variavel != null && variavel.getHead() != null
            && coluna != null && coluna.getHead() != null) {
                Tipo tipoVariavel = ambiente.get(variavel.getHead());   // lança se não declarada
                if (!tipoVariavel.eIgual(coluna.getHead().getTipo())) {
                    return ErroTipo.erro("a variavel " + variavel.getHead()
                            + " nao tem o tipo da coluna " + coluna.getHead().getId());
                }
        variavel = variavel.getTail();
        coluna = coluna.getTail();
        }
        // As duas listas precisam terminar juntas, como no mesmoEsquema.
        boolean fimVariaveis = variavel == null || variavel.getHead() == null;
        boolean fimColunas = coluna == null || coluna.getHead() == null;
        return (fimVariaveis && fimColunas)
                || ErroTipo.erro("o numero de variaveis do into e diferente do numero de colunas");

    }
    /**
     * Devolve as colunas lidas pelo select, com seus tipos, na ordem do
     * resultado: todas as da tabela no select *, ou as pedidas, na ordem
     * do select.
     */
    private Lista<Coluna> colunasSelecionadas(Lista<Coluna> esquema) {
        if (colunas == null) {
            return esquema;
        }
        return buscarColunas(colunas, esquema);
    }

    /**
     * Monta a lista de colunas do esquema correspondentes aos nomes, na
     * ordem dos nomes.
     */
    private Lista<Coluna> buscarColunas(Lista<Id> nomes, Lista<Coluna> esquema) {
        if (nomes == null || nomes.getHead() == null) {
            return new Lista<Coluna>();
        }
        return new Lista<Coluna>(buscarColuna(esquema, nomes.getHead()),
                buscarColunas(nomes.getTail(), esquema));
    }

    /**
     * Devolve a coluna do esquema com o nome dado.
     */
    private Coluna buscarColuna(Lista<Coluna> esquema, Id nome) {
        Lista<Coluna> aux = esquema;
        while (aux != null && aux.getHead() != null) {
            if (aux.getHead().getId().equals(nome)) {
                return aux.getHead();
            }
            aux = aux.getTail();
        }
        return null;
    }

    @Override
    public String toString() {
        return "SELECT " + (colunas == null ? "*" : colunas.toString())
                + (variaveis == null ? "" : " INTO " + variaveis)
                + " FROM " + id
                + (condicao == null ? "" : " WHERE " + condicao)
                + (comando == null ? "" : " DO " + comando);
    }
}