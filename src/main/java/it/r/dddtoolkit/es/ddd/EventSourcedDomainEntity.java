package it.r.dddtoolkit.es.ddd;

import it.r.dddtoolkit.ddd.DomainEntity;
import it.r.dddtoolkit.ddd.DomainEvent;
import it.r.dddtoolkit.es.eventstore.EventStream.Version;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import lombok.NonNull;

/**
 * Classe base per un Entity di dominio funzionante tramite EventSourcing.
 * 
 * Una classe che implementa EventSourcedDomainEntity dovrà obbligatoriamente definire un costruttore che prenda
 * in ingresso lo stato relativo dell'entity, delegandolo a {@link #EventSourcedDomainEntity(it.r.dddtoolkit.es.ddd.DomainEntityState)} . Esempio:
 * <pre>
 * protected Entity(EntityState state) {
 *     super(state);
 * }
 * </pre>
 * 
 * L'entity esporrà i metodi pubblici per modificarla. Ognuno di questi metodi viene implementato nella forma:
 * <pre>
 * public void metodo(parametri...){
 *     //verifica se possibile effettuare l'azione
 * 
 *     final DomainEvent event = ... //inizializzazione evento specifico
 *     apply(event)
 * }
 * </pre>
 * 
 * Il metodo {@link #apply(it.r.dddtoolkit.ddd.DomainEvent) } permette di modificare l'entity e aggiungere l'evento
 * alla lista degli eventi da salvare.
 * @author rascioni
 * @param <S>
 * @param <ID> 
 * @see DomainEntityState
 */
public abstract class EventSourcedDomainEntity<S extends DomainEntityState<ID>, ID extends Serializable> extends DomainEntity<ID> {

    private S state;

    private List<DomainEvent> uncommittedEvents;
    
    protected EventSourcedDomainEntity(@NonNull S state) {
    	this.uncommittedEvents = new ArrayList<DomainEvent>();
    	this.state = state;
    }
    
    protected S state() {
    	if (this.state == null) {
    		throw new IllegalStateException("State not initialized");
    	}
    	return this.state;
    }
    
    protected void updateTo(Version version) {
    	this.state().updateTo(version);
    	this.uncommittedEvents.clear();
    }
    
    @Override
    public ID identity() {
        return state().identity();
    }

    protected void apply(DomainEvent aDomainEvent) {
        this.state().mutate(aDomainEvent);
        this.uncommittedEvents.add(aDomainEvent);
    }

    protected Version version() {
        return this.state.version();
    }

    protected List<DomainEvent> mutatingEvents() {
		return uncommittedEvents;
	}

}
