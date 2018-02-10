package it.r.dddtoolkit.ddd;

import it.r.dddtoolkit.core.Context;

/**
 * Interfaccia generale per i repository di dominio. Un Repository è associato
 * ad un entità del dominio, nel caso di un Aggregate sarà associato SOLAMENTE
 * al corrispettivo AGGREGATE ROOT
 *
 * @author rascioni
 * @param <D>
 */
public interface AggregateRepository<D extends Aggregate<?, C>, C extends Context> {
    /**
     * Conserva all'interno del repository l'entity passata
     * @param entity 
     */
    void store(D entity);

    /**
     * Ricerca all'iterno del repository un entity tramite la sua identità
     * @param context
     * @return
     */
    D findByIdentity(C context);

    /**
     * Controlla se il repository contiene una determinata entity.
     * @param context
     * @return
     */
    default boolean contains(C context) {
        return findByIdentity(context) != null;
    }
}
