package it.r.dddtoolkit.modules.es.ddd;

import com.google.common.collect.ImmutableList;
import it.r.dddtoolkit.ddd.Aggregate;
import it.r.dddtoolkit.core.DomainEvent;
import it.r.dddtoolkit.modules.es.EventTransaction;
import it.r.dddtoolkit.core.Context;
import it.r.dddtoolkit.modules.es.eventstore.EventStream;
import it.r.dddtoolkit.modules.es.eventstore.Version;
import it.r.dddtoolkit.modules.es.support.AggregateMutator;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import static it.r.dddtoolkit.modules.es.eventstore.EventStream.lastVersion;

/**
 * Classe base per un Aggregate di dominio funzionante tramite EventSourcing.
 *
 * 
 * Il metodo {@link #apply(DomainEvent) } permette di modificare l'entity e aggiungere l'evento
 * alla lista degli eventi da salvare.
 * @author rascioni
 * @param <S>
 */
public abstract class EventSourcedAggregate<S, C extends Context> extends Aggregate<S> {

    private final AggregateMutator<S> mutator = AggregateMutator.of(this);

    private List<DomainEvent> uncommittedEvents;
    
    protected EventSourcedAggregate(String aggregateId) {
        this(aggregateId, null);
    }
    protected EventSourcedAggregate(String aggregateId, S state) {
        super(aggregateId, state);
        this.uncommittedEvents = new ArrayList<>();
    }
    
    public final <E extends DomainEvent> void apply(E aDomainEvent) {
        this.state = mutator.handlerFor(aDomainEvent)
            .apply(aDomainEvent, state);
        this.uncommittedEvents.add(aDomainEvent);
    }

    protected void load(EventStream<C> eventStream) {
        eventStream.events()
            .forEach(e -> apply(e.getEvent()));

        resetTo(lastVersion(eventStream));
    }

    protected EventTransaction<C> commit(C context) {
        final Version next = this.version.next();

        final EventTransaction transaction = new EventTransaction(
            identity(),
            next,
            ImmutableList.copyOf(uncommittedEvents),
            context
        );

        resetTo(next);

        return transaction;
	}

    private void resetTo(Version next) {
        this.version = next;
        this.uncommittedEvents.clear();
    }
}
