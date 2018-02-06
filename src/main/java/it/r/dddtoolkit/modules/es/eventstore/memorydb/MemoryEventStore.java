package it.r.dddtoolkit.modules.es.eventstore.memorydb;

import static com.google.common.base.Preconditions.checkState;
import static com.google.common.collect.FluentIterable.from;
import static it.r.dddtoolkit.modules.es.eventstore.EventStream.lastVersion;
import static java.util.Comparator.comparing;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import it.r.dddtoolkit.modules.es.EventTransaction;
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
    protected final SortedMap<Long, EventTransaction<C>> index;

    public MemoryEventStore() {
        this.history = new ConcurrentHashMap<>();
        this.index = new TreeMap<>(Comparator.<Long>naturalOrder().reversed());
    }

    @Override
    public Version append(EventTransaction<C> tx, Version expectedVersion) {
        final Version nextVersion = expectedVersion.next();

        try {
            history.compute(tx.getAggregateId(), (s, commits) -> {

                final List<EventTransaction<C>> result;
                if (commits == null) {
                    result = Arrays.asList(tx);
                }
                else {
                    final Version actual = lastVersion(commits);

                    checkState(actual.equals(expectedVersion),
                        "Trying to append to an event stream with a different version. Current %s - Expected %s", actual, expectedVersion);

                    log.trace("Appending to stream {} expected version: {} - actual: {}", tx.getAggregateId(), expectedVersion, actual);

                    final List<EventTransaction<C>> newEventTransactions = new ArrayList<>();
                    newEventTransactions.addAll(commits.getTransactions());
                    newEventTransactions.add(tx);

                    index.put(nextVersion.getTimestamp(), tx);

                    result = ImmutableList.copyOf(newEventTransactions);
                }

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
    public EventStream eventStream(String streamId) {
        return history.getOrDefault(streamId, new EventStream(Collections.emptyList()));
    }

    @Override
    public EventStream happenedFrom(Version version) {
        final SortedMap<Long, EventTransaction<C>> commits = index.headMap(version.getTimestamp());

        final List<EventTransaction<C>> events = commits.values()
            .stream()
            .sorted(comparing(commit -> -commit.getVersion().getTimestamp()))
            .collect(Collectors.toList());

        return new EventStream(Lists.reverse(events));
    }
}
