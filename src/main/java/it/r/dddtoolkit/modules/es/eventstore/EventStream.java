package it.r.dddtoolkit.modules.es.eventstore;

import java.util.List;

import it.r.dddtoolkit.core.DomainEvent;
import it.r.dddtoolkit.modules.es.ddd.AggregateTransaction;
import it.r.dddtoolkit.core.Context;
import lombok.Value;

import java.util.Map;
import java.util.SortedMap;
import java.util.stream.Stream;

@Value
public class EventStream<C extends Context> {

    SortedMap<Version, AggregateTransaction<C>> transactions;

    public Stream<EventInfo> events() {
        return transactions.entrySet().stream()
            .flatMap(entry -> {
                final AggregateTransaction<C> transaction = entry.getValue();
                final Version version = entry.getKey();

                return transaction.getEvents()
                    .stream()
                    .map(event -> new EventInfo(version, transaction.getContext(), event));
            });
    }

    public boolean hasTransactions() {
        return !transactions.isEmpty();
    }

    @Value
    public static class EventInfo<C extends Context> {
        private Version version;
        private C context;
        private DomainEvent event;
    }

    public static Version lastVersion(EventStream stream) {
        final SortedMap<Version, List<AggregateTransaction>> transactions = stream.getTransactions();

        return transactions.isEmpty()
            ? Version.UNINITIALIZED
            : transactions.lastKey();
    }
}
