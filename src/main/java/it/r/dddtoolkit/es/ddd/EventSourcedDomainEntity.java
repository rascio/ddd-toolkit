package it.r.dddtoolkit.es.ddd;

import it.r.dddtoolkit.ddd.DomainEntity;
import it.r.dddtoolkit.ddd.DomainEvent;
import it.r.dddtoolkit.es.eventstore.EventStream.Version;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import lombok.NonNull;

/**
 * 
 * @author rascioni
 * @param <S>
 * @param <ID> 
 */
public abstract class EventSourcedDomainEntity<S extends DomainEntityState<ID>, ID extends Serializable> extends DomainEntity<ID> {

    private S state;

    private List<DomainEvent> mutatingEvents;
    
    protected EventSourcedDomainEntity(@NonNull S state) {
    	this.mutatingEvents = new ArrayList<DomainEvent>();
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
    	this.mutatingEvents.clear();
    }
    
    @Override
    public ID identity() {
        return state().identity();
    }

    protected void apply(DomainEvent aDomainEvent) {
        this.state().mutate(aDomainEvent);
        this.mutatingEvents.add(aDomainEvent);
    }

    protected Version version() {
        return this.state.version();
    }

    protected List<DomainEvent> mutatingEvents() {
		return mutatingEvents;
	}

}
