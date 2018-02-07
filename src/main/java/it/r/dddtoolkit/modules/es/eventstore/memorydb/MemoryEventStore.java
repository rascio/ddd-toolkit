package it.r.dddtoolkit.modules.es.eventstore.memorydb;

import static com.google.common.base.Preconditions.checkState;
import static com.google.common.collect.FluentIterable.from;
import static it.r.dddtoolkit.modules.es.eventstore.EventStream.lastVersion;
import static java.util.Comparator.comparing;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import it.r.dddtoolkit.modules.es.ddd.AggregateTransaction;
import it.r.dddtoolkit.core.Context;
import it.r.dddtoolkit.modules.es.eventstore.EventStore;
import it.r.dddtoolkit.modules.es.eventstore.EventStream;
import it.r.dddtoolkit.modules.es.eventstore.Version;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MemoryEventStore<C extends Context> implements EventStore<C> {

    protected final Map<String, EventStream<C>> history;
    protected final SortedMap<Version, AggregateTransaction<C>> index;

    public MemoryEventStore() {
        this.history = new ConcurrentHashMap<>();
        this.index = new TreeMap<>(Comparator.comparing(Version::getTimestamp));
    }

    @Override
    public Version append(AggregateTransaction<C> tx, Version expectedVersion) {
        final Version nextVersion = expectedVersion.next();

        try {
            history.compute(tx.getStreamId(), (s, eventStream) -> {

                final SortedMap<Version, AggregateTransaction<C>> result;
                if (eventStream == null) {
                    result = new TreeMap<>(Comparator.comparing(Version::getNumber));
                }
                else {
                    final Version actual = lastVersion(eventStream);

                    checkState(actual.equals(expectedVersion),
                        "Trying to append to an event stream with a different version. Current %s - Expected %s", actual, expectedVersion);

                    log.trace("Appending to stream {} expected version: {} - actual: {}", tx.getStreamId(), expectedVersion, actual);

                    result = new TreeMap<>(eventStream.getTransactions());
                }
                result.put(nextVersion, tx);
                index.put(nextVersion, tx);

                return new EventStream<>(result);
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
        return history.getOrDefault(streamId, new EventStream(Collections.emptySortedMap()));
    }

    @Override
    public EventStream<C> happenedFrom(Version version) {
        final SortedMap<Version, AggregateTransaction<C>> commits = new TreeMap<>(index.tailMap(version));

        if (version.equals(index.lastKey())) {
            commits.remove(commits.lastKey()); //tailMap is >=, but we want >
        }

        return new EventStream(commits);
    }
}
