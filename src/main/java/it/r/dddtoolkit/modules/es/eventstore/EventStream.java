package it.r.dddtoolkit.modules.es.eventstore;

import java.util.List;

import it.r.dddtoolkit.core.DomainEvent;
import it.r.dddtoolkit.modules.es.EventTransaction;
import it.r.dddtoolkit.core.Context;
import lombok.Value;

import java.util.stream.Stream;

@Value
public class EventStream<C extends Context> {

    List<EventTransaction<C>> transactions;

    public Stream<EventInfo> events() {
        return transactions.stream()
            .flatMap(tx -> tx.getEvents().stream()
                .map(event -> new EventInfo(tx.getVersion(), tx.getAggregateId(), event))
            );
    }

    public boolean hasTransactions() {
        return !transactions.isEmpty();
    }

    @Value
    public static class EventInfo {

        private Version version;
        private String aggregateId;
        private DomainEvent event;
    }

    public static Version lastVersion(EventStream<?> stream) {
        final List<? extends EventTransaction<?>> transactions = stream.getTransactions();

        return transactions.isEmpty()
            ? Version.UNINITIALIZED
            : transactions.get(transactions.size() - 1).getVersion();
    }
}
