package li1.plp.imperative1.command;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

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
import li1.plp.imperative1.util.Lista;
import li1.plp.imperative1.util.Coluna;
import li1.plp.imperative1.util.ErroTipo;
import li1.plp.imperative1.util.SqliteManager;

/**
 * Representa o comando {@code select L from T [where e]} da ImpSQL.
 * {@code colunas == null} representa o {@code *}, e
 * {@code condicao == null} representa a ausência de {@code where}.
 */
public class Select implements Comando {

    private Id id;
    private Lista<Id> colunas;
    private Expressao condicao;

    public Select(Id id, Lista<Id> colunas, Expressao condicao) {
        this.id = id;
        this.colunas = colunas;
        this.condicao = condicao;
    }

    /**
     * Lê os registros da tabela, filtra com o where e escreve na saída
     * os valores das colunas selecionadas.
     */
    @Override
    public AmbienteExecucaoImperativa executar(AmbienteExecucaoImperativa ambiente)
            throws IdentificadorJaDeclaradoException, IdentificadorNaoDeclaradoException,
            EntradaVaziaException, ErroTipoEntradaException {

        List<LinkedHashMap<Id, Valor>> registros = SqliteManager.getInstance().lerRegistros(id);

        for (LinkedHashMap<Id, Valor> registro : registros) {
            if (condicao == null || satisfazCondicao(registro, ambiente)) {
                escreverRegistro(registro, ambiente);
            }
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
     * Verifica se o select é bem tipado: a tabela foi definida por um
     * create-use, as colunas pedidas existem na tabela e, se houver where,
     * a condição é bem tipada e booleana.
     */
    @Override
    public boolean checaTipo(AmbienteCompilacaoImperativa ambiente)
            throws IdentificadorJaDeclaradoException, IdentificadorNaoDeclaradoException,
            EntradaVaziaException {

        // Esquema da tabela
        Lista<Coluna> esquema = ambiente.getTabela(id);

        return esquema != null                              // a tabela foi definida, nunca vem null
                && colunasExistem(esquema)                  // as colunas pedidas existem
                && condicaoBemTipada(ambiente, esquema);    // o where é booleano
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
        Lista<Coluna> aux = esquema;
        while (aux != null && aux.getHead() != null) {
            if (aux.getHead().getId().equals(nome)) {
                return true;
            }
            aux = aux.getTail();
        }
        return false;
    }

    @Override
    public String toString() {
        return "SELECT " + (colunas == null ? "*" : colunas.toString())
                + " FROM " + id + (condicao == null ? "" : " WHERE " + condicao);
    }
}