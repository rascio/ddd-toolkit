package it.r.dddtoolkit.es.eventstore.mongodb;

import com.fasterxml.jackson.databind.ObjectMapper;
import static com.google.common.collect.FluentIterable.from;
import it.r.dddtoolkit.ddd.DomainEvent;
import it.r.dddtoolkit.es.ApplicationEvent;
import it.r.dddtoolkit.es.eventstore.EventStore;
import it.r.dddtoolkit.es.eventstore.EventStream;
import it.r.dddtoolkit.es.eventstore.EventStream.Version;
import it.r.dddtoolkit.es.support.EventPublisher;

import java.util.ArrayList;
import java.util.List;

import org.bson.types.ObjectId;
import org.mongojack.DBCursor;
import org.mongojack.DBSort;
import org.mongojack.JacksonDBCollection;

import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import lombok.AccessLevel;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@SuppressWarnings("rawtypes")
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@Slf4j
public class MongoDbCollectionEventStore implements EventStore {

    @NonNull
    private final JacksonDBCollection<MongoDbEventDescriptor, ObjectId> collection;

    public MongoDbCollectionEventStore(DBCollection collection, ObjectMapper mapper) {
        this(JacksonDBCollection.wrap(collection, MongoDbEventDescriptor.class, ObjectId.class, mapper));
    }

    public MongoDbCollectionEventStore(DBCollection collection, EventPublisher eventPublisher) {
        this(collection, new ObjectMapper());
    }

    @Override
    public EventStream eventStream(String streamId) {

        final DBCursor<MongoDbEventDescriptor> cursor = collection.find().is("streamIdentity", streamId).sort(byHappenedDate());
        final List<ApplicationEvent<DomainEvent>> events = new ArrayList<>(cursor.size());

        Version version = Version.UNINITIALIZED;
        while (cursor.hasNext()) {
            final MongoDbEventDescriptor<?> eventDescriptor = cursor.next();
            events.add(eventDescriptor.getEvent());
            version = Version.of(eventDescriptor.getVersion());
        }

        return new EventStream(ImmutableList.copyOf(events), version);
    }

    @Override
    public Version append(String streamIdentifier, List<ApplicationEvent<DomainEvent>> events, Version expectedVersion) {
        final Version actualVersion = versionOf(streamIdentifier);
        if (actualVersion.isGreater(expectedVersion)) {
            throw new IllegalStateException(String.format("Concurrent modification of stream [%s] expected version %s but was %s", streamIdentifier, expectedVersion, actualVersion));
        }
        final Version nextVersion = actualVersion.next();

        final List<MongoDbEventDescriptor> eventDescriptors = from(events).transform(toEventDescriptors(nextVersion, streamIdentifier)).toList();

        log.debug("Appending events to stream {}", streamIdentifier);
        collection.insert(eventDescriptors);

        return nextVersion;
    }

    @Override
    public List<ApplicationEvent<DomainEvent>> history() {
        return from(collection.find().sort(byHappenedDate()).toArray()).transform(new Function<MongoDbEventDescriptor, ApplicationEvent<DomainEvent>>() {

            @Override
            public ApplicationEvent<DomainEvent> apply(MongoDbEventDescriptor input) {
                return input.getEvent();
            }

        }).toList();
    }

    private Version versionOf(String stream) {
        final Optional<MongoDbEventDescriptor> lastEvent = lastEventOf(stream);
        return lastEvent.isPresent() ? Version.of(lastEvent.get().getVersion()) : Version.UNINITIALIZED;
    }

    private Optional<MongoDbEventDescriptor> lastEventOf(String streamId) {
        DBCursor<MongoDbEventDescriptor> descriptors = collection.find().is("streamIdentity", streamId).sort(byHappenedDate());

        return descriptors.hasNext() ? Optional.of(descriptors.next()) : Optional.<MongoDbEventDescriptor>absent();
    }

    private Function<ApplicationEvent<?>, MongoDbEventDescriptor> toEventDescriptors(final Version version, final String streamIdentity) {
        return new Function<ApplicationEvent<?>, MongoDbEventDescriptor>() {
            @SuppressWarnings({"unchecked"})
            @Override
            public MongoDbEventDescriptor<?> apply(ApplicationEvent<?> input) {
                return new MongoDbEventDescriptor(null, input, streamIdentity, version.number());
            }
        };
    }

    private DBObject byHappenedDate() {
        return DBSort.desc("event.happened");
    }
}
