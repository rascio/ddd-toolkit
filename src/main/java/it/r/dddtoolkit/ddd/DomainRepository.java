package it.r.dddtoolkit.ddd;

/**
 * Interfaccia generale per i repository di dominio. Un Repository è associato
 * ad un entità del dominio, nel caso di un Aggregate sarà associato SOLAMENTE
 * al corrispettivo AGGREGATE ROOT
 *
 * @author rascioni
 * @param <D>
 */
public interface DomainRepository<D extends Aggregate<?>> {
    /**
     * Conserva all'interno del repository l'entity passata
     * @param entity 
     */
    void store(D entity);

    /**
     * Ricerca all'iterno del repository un entity tramite la sua identità
     * @param aggregateId
     * @return 
     */
    D findByIdentity(String aggregateId);

    /**
     * Controlla se il repository contiene una determinata entity.
     * @param aggregateId
     * @return 
     */
    default boolean contains(String aggregateId) {
        return findByIdentity(aggregateId) != null;
    }
}
