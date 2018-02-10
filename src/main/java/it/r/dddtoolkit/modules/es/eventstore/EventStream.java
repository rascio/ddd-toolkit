package it.r.dddtoolkit.modules.es.eventstore;

import it.r.dddtoolkit.core.Context;
import it.r.dddtoolkit.core.DomainEvent;
import it.r.dddtoolkit.modules.es.ddd.EventsTransaction;
import lombok.Value;
import org.apache.commons.lang3.tuple.Pair;

import java.util.stream.Stream;

public interface EventStream<C extends Context> {

    Stream<Pair<Version, EventsTransaction<C>>> transactions();
    Version lastVersion();
    boolean hasTransactions();

    default Stream<EventInfo<C>> events() {
        return transactions()
            .flatMap(pair -> {
                final EventsTransaction<C> transaction = pair.getRight();
                final Version version = pair.getLeft();

                return pair.getRight()
                    .getEvents()
                    .stream()
                    .map(event -> new EventInfo<>(version, transaction.getContext(), event));
            });
    }
    @Value
    class EventInfo<C extends Context> {
        private Version version;
        private C context;
        private DomainEvent event;

    }
}
