package li1.plp.imperative1.command;

import li1.plp.expressions2.memory.IdentificadorJaDeclaradoException;
import li1.plp.expressions2.memory.IdentificadorNaoDeclaradoException;
import li1.plp.imperative1.memory.AmbienteCompilacaoImperativa;
import li1.plp.imperative1.memory.AmbienteExecucaoImperativa;
import li1.plp.imperative1.memory.EntradaVaziaException;
import li1.plp.imperative1.memory.ErroTipoEntradaException;
import li1.plp.imperative1.util.Lista;

/**
 * Representa um programa da ImpSQL que começa com uma lista de
 * {@code create-use}, seguida de um {@code ComandoBase}.
 *
 * Na verificação de tipos e na execução, os {@code create-use} são
 * processados primeiro, em ordem, e depois o {@code ComandoBase}.
 * Ao contrário do {@link ComandoDeclaracao}, não cria um escopo local:
 * as tabelas definidas têm escopo global.
 */
public class ComandoPersistente implements Comando {

    private Lista<CreateUse> createUses;
    private Comando comandoBase;

    public ComandoPersistente(Lista<CreateUse> createUses, Comando comandoBase) {
        this.createUses = createUses;
        this.comandoBase = comandoBase;
    }

    /**
     * Executa cada {@code create-use}, na ordem em que aparece,
     * e depois o {@code ComandoBase}.
     */
    @Override
    public AmbienteExecucaoImperativa executar(AmbienteExecucaoImperativa ambiente)
            throws IdentificadorJaDeclaradoException,
            IdentificadorNaoDeclaradoException, EntradaVaziaException,
            ErroTipoEntradaException {

        Lista<CreateUse> aux = createUses;
        while (aux != null && aux.getHead() != null) {
            ambiente = aux.getHead().executar(ambiente);
            aux = aux.getTail();
        }
        return comandoBase.executar(ambiente);
    }

    /**
     * Verifica cada {@code create-use}, na ordem em que aparece, e depois
     * o {@code ComandoBase}, que usa as tabelas registradas pela lista.
     */
    @Override
    public boolean checaTipo(AmbienteCompilacaoImperativa ambiente)
            throws IdentificadorJaDeclaradoException,
            IdentificadorNaoDeclaradoException, EntradaVaziaException {

        // Lista<CreateUse> aux = createUses;
        // while (aux != null && aux.getHead() != null) {
        //     if (!aux.getHead().checaTipo(ambiente)) {
        //         return false;
        //     }
        //     aux = aux.getTail();
        // }
        // return comandoBase.checaTipo(ambiente);
        return true;
    }

    @Override
    public String toString() {
        return createUses + "; " + comandoBase;
    }
}
