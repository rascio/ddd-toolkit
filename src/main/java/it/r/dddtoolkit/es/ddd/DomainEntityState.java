package it.r.dddtoolkit.es.ddd;

import it.r.dddtoolkit.ddd.DomainEvent;
import it.r.dddtoolkit.es.eventstore.EventStream.Version;

import java.util.List;

import lombok.AccessLevel;
import lombok.Setter;

/**
 * Classe base per lo stato di un entità del dominio con funzionalità di event
 * sourcing. Una classe che implementa DomainEntityState dovrà definire
 * OBBLIGATORIAMENTE un costruttore che prenda in ingresso l'identità
 * dell'entity corrispondente. Inoltre la classe dovrà trovarsi nello stesso
 * package dell'entità ed avere una naming convention ${nomeEntita}Class.
 *
 * La classe stato dell'entità dovrà anche definire dei metodi
 * `when(DomainEvent)` per ogni domain event generato dalla rispettiva entità.
 *
 * @author rascioni
 * @param <ID>
 */
public abstract class DomainEntityState<ID> {

    private static final String MUTATOR_METHOD_NAME = "when";

	private final EntityBehavior behavior;

    private Version version;

    @Setter(AccessLevel.PACKAGE)
    private ID identity;

    public DomainEntityState(ID id, Version v) {
        this.identity = id;
        this.version = v;
		this.behavior = init();
    }

    protected DomainEntityState(ID id) {
        this(id, Version.UNINITIALIZED);
    }

    protected DomainEntityState() {
        this(null);
    }

    public ID identity() {
        return identity;
    }

    public Version version() {
        return this.version;
    }

    void updateTo(Version v) {
        this.version = v;
    }

    void loadFromHistory(List<DomainEvent> events, Version version) {

        for (DomainEvent event : events) {
            this.mutate(event);
        }

        this.version = version;
    }
	
	protected abstract EntityBehavior init();

    protected void mutate(DomainEvent aDomainEvent) {
		behavior.happened(aDomainEvent);
    }

}
