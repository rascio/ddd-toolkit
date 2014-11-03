package it.r.dddtoolkit.ddd;

import java.io.Serializable;

/**
 * Interfaccia generale per i repository di dominio. Un Repository è associato
 * ad un entità del dominio, nel caso di un Aggregate sarà associato SOLAMENTE
 * al corrispettivo AGGREGATE ROOT
 *
 * @author rascioni
 * @param <D>
 * @param <ID>
 */
public interface DomainRepository<D extends DomainEntity<ID>, ID extends Serializable> {
    /**
     * Conserva all'interno del repository l'entity passata
     * @param entity 
     */
    void store(D entity);

    /**
     * Ricerca all'iterno del repository un entity tramite la sua identità
     * @param domainIdentity
     * @return 
     */
    D findByIdentity(ID domainIdentity);

    /**
     * Controlla se il repository contiene una determinata entity.
     * @param domainIdentity
     * @return 
     */
    boolean contains(ID domainIdentity);
}
