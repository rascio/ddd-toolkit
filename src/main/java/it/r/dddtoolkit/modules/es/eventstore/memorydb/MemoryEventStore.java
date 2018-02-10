package it.r.dddtoolkit.modules.es.eventstore.memorydb;

import it.r.dddtoolkit.core.Context;
import it.r.dddtoolkit.modules.es.ddd.EventsTransaction;
import it.r.dddtoolkit.modules.es.eventstore.EventStore;
import it.r.dddtoolkit.modules.es.eventstore.EventStream;
import it.r.dddtoolkit.modules.es.eventstore.Version;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;

import static com.google.common.base.Preconditions.checkState;

@Slf4j
public class MemoryEventStore<C extends Context> implements EventStore<C> {

    protected final Map<String, SortedMap<Version, EventsTransaction<C>>> history;
    protected final SortedMap<Version, EventsTransaction<C>> index;

    public MemoryEventStore() {
        this.history = new ConcurrentHashMap<>();
        this.index = Collections.synchronizedSortedMap(new TreeMap<>(Comparator.comparing(Version::getTimestamp)));
    }

    @Override
    public Version append(EventsTransaction<C> tx, Version expectedVersion) {
        final Version nextVersion = expectedVersion.next();

        try {
            history.compute(tx.getStreamId(), (s, events) -> {

                final SortedMap<Version, EventsTransaction<C>> result;
                if (events == null) {
                    result = new TreeMap<>(Comparator.comparing(Version::getNumber));
                }
                else {
                    final Version actual = events.lastKey();

                    checkState(actual.equals(expectedVersion),
                        "Trying to append to an event stream with a different version. Current %s - Expected %s", actual, expectedVersion);

                    log.trace("Appending to stream {} expected version: {} - actual: {}", tx.getStreamId(), expectedVersion, actual);

                    result = events;
                }
                result.put(nextVersion, tx);
                index.put(nextVersion, tx);

                return result;
            });

        }
        catch (RuntimeException e) {

            index.remove(nextVersion.getTimestamp());
            throw e;
        }

        return nextVersion;

    }

    @Override
    public EventStream<C> eventStream(String streamId) {
        return eventStream(history.getOrDefault(streamId, Collections.emptySortedMap()));
    }

    @Override
    public EventStream<C> happenedFrom(Version version) {
        final SortedMap<Version, EventsTransaction<C>> commits = new TreeMap<>(index.tailMap(version));

        if (version.equals(index.lastKey())) {
            commits.remove(commits.lastKey()); //tailMap is >=, but we want >
        }

        return eventStream(commits);
    }

    private EventStream<C> eventStream(SortedMap<Version, EventsTransaction<C>> transactions) {
        return new EventStream<C>() {

            @Override
            public Stream<Pair<Version, EventsTransaction<C>>> transactions() {
                return transactions.entrySet()
                    .stream()
                    .map(e -> Pair.of(e.getKey(), e.getValue()));

            }

            @Override
            public Version lastVersion() {
                return transactions.isEmpty()
                    ? Version.UNINITIALIZED
                    : transactions.lastKey();
            }

            @Override
            public boolean hasTransactions() {
                return !transactions.isEmpty();
            }
        };
    }
}
