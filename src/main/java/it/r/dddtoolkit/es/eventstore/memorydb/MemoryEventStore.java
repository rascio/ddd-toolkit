package it.r.dddtoolkit.es.eventstore.memorydb;

import com.google.common.collect.ImmutableList;
import it.r.dddtoolkit.ddd.DomainEvent;
import it.r.dddtoolkit.es.ApplicationEvent;
import it.r.dddtoolkit.es.eventstore.EventStore;
import it.r.dddtoolkit.es.eventstore.EventStream;
import it.r.dddtoolkit.es.eventstore.EventStream.Version;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MemoryEventStore implements EventStore {

    protected Map<String, EventStream> memoryStore;

    public MemoryEventStore() {
        this.memoryStore = Collections.synchronizedMap(new HashMap<String, EventStream>());
    }

    @Override
    public Version append(final String identifier, final List<ApplicationEvent<DomainEvent>> events, Version expectedVersion) {

        EventStream stream = getOrCreate(identifier);
        log.trace("Appending to stream {} expected version: {} - actual: {}", identifier, expectedVersion, stream.version());

        if (!stream.version().equals(expectedVersion)) {
            throw new IllegalStateException(String.format("Trying to append to an event stream with a different version. Current %s - Expected %s", stream.version(), expectedVersion));
        }
        stream = stream.append(events);
        memoryStore.put(identifier, stream);

        return stream.version();
    }

    @Override
    public EventStream eventStream(String streamId) {
        return getOrCreate(streamId);
    }

    private EventStream getOrCreate(String streamIdentifier) {
        EventStream eventStream = memoryStore.get(streamIdentifier);
        if (eventStream == null) {
            eventStream = new EventStream(new LinkedList<ApplicationEvent<DomainEvent>>(), Version.UNINITIALIZED);
            log.trace("Stream {} doesn't exists creating new one. Version: ", streamIdentifier, eventStream.version());
            memoryStore.put(streamIdentifier, eventStream);
        }
        return eventStream;
    }

    @Override
    public List<ApplicationEvent<DomainEvent>> history() {
        return ImmutableList.of();
    }

    
}
