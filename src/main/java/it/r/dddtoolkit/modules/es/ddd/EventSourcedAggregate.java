package it.r.dddtoolkit.modules.es.ddd;

import com.google.common.collect.ImmutableList;
import it.r.dddtoolkit.core.Context;
import it.r.dddtoolkit.core.DomainEvent;
import it.r.dddtoolkit.ddd.Aggregate;
import it.r.dddtoolkit.modules.es.eventstore.EventStream;
import it.r.dddtoolkit.modules.es.eventstore.Version;
import it.r.dddtoolkit.modules.es.support.AggregateMutator;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Classe base per un Aggregate di dominio funzionante tramite EventSourcing.
 *
 * 
 * Il metodo {@link #apply(DomainEvent) } permette di modificare l'entity e aggiungere l'evento
 * alla lista degli eventi da salvare.
 * @author rascioni
 * @param <S>
 */
@Slf4j
public abstract class EventSourcedAggregate<S, C extends Context> extends Aggregate<S, C> {

    private final AggregateMutator<S> mutator = AggregateMutator.of(this);

    private List<DomainEvent> uncommittedEvents;
    
    protected EventSourcedAggregate(C context) {
        this(null, context);
    }
    protected EventSourcedAggregate(S state, C context) {
        super(context, state);
        this.uncommittedEvents = new ArrayList<>();
    }
    
    public final <E extends DomainEvent> void apply(E aDomainEvent) {
        log.trace("EventSourcedAggregate.apply({})", aDomainEvent);
        log.trace("{}@{} => {}", identity(), version(), state);
        this.state = mutator.handlerFor(aDomainEvent)
            .apply(aDomainEvent, state);
        log.trace("==> {}", state);
        this.uncommittedEvents.add(aDomainEvent);
    }

    protected void load(EventStream<?> eventStream) {
        log.trace("Loading: {}", eventStream.events().collect(Collectors.toList()));
        eventStream.events()
            .forEach(e -> apply(e.getEvent()));

        resetTo(eventStream.lastVersion());
    }

    protected void commit(Function<List<DomainEvent>, Version> next) {
        final Version version = next.apply(ImmutableList.copyOf(uncommittedEvents));

        resetTo(version);
	}

    final void resetTo(Version next) {
        this.version = next;
        this.uncommittedEvents.clear();
    }
}
