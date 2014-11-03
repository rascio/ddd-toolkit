package it.r.dddtoolkit.es.ddd;

import static com.google.common.collect.FluentIterable.from;
import it.r.dddtoolkit.ddd.DomainEvent;
import it.r.dddtoolkit.ddd.DomainRepository;
import it.r.dddtoolkit.es.ApplicationEvent;
import it.r.dddtoolkit.es.eventstore.EventStore;
import it.r.dddtoolkit.es.eventstore.EventStream;
import it.r.dddtoolkit.es.eventstore.EventStream.Version;
import it.r.dddtoolkit.es.support.DefaultEventStreamIdentifierFromEntity;
import it.r.dddtoolkit.es.support.EventStreamIdentifierFromEntity;

import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import lombok.AccessLevel;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import com.google.common.base.Function;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@Slf4j
public class EventSourcingDomainRepository<E extends EventSourcedDomainEntity<S, ID>, S extends DomainEntityState<ID>, ID extends Serializable> implements DomainRepository<E, ID> {

    private final EventStore eventStore;
    private final Class<E> entityClass;
    private final EventStreamIdentifierFromEntity<ID> identifierFromEntity;
    private final DomainEntityStateFactory domainEntityStateFactory = new DomainEntityStateFactory();
    private final List<PreStoreEventInterceptor> interceptors;

    public EventSourcingDomainRepository(@NonNull EventStore eventStore, @NonNull Class<E> entityClass, @NonNull List<PreStoreEventInterceptor> interceptors) {
        this(eventStore, entityClass, new DefaultEventStreamIdentifierFromEntity<ID>(), interceptors);
    }

    @Override
    public E findByIdentity(ID domainIdentity) {
        final EventStream eventStream = eventStore.eventStream(identifierFromEntity.streamIdentifierOf(entityClass, domainIdentity));

        if (eventStream.version() == Version.UNINITIALIZED) {
            return null;
        }

        final DomainEntityState<ID> state = domainEntityStateFactory.create(entityClass, domainIdentity, eventStream);

        return create(entityClass, state);
    }

    @Override
    public void store(E entity) {

        final Map<String, Object> headers = new HashMap<>();
        headers.put(ApplicationEvent.AGGREGATE_ID, entity.identity());
        headers.put(ApplicationEvent.OCCURED_ON, new Date());
        headers.put(ApplicationEvent.EVENT_IDENTIFIER, UUID.randomUUID());

        final List<ApplicationEvent<DomainEvent>> applicationEvents = from(entity.mutatingEvents()).transform(toApplicationEvent(headers)).toList();

        for (PreStoreEventInterceptor interceptor : interceptors) {
            for (ApplicationEvent<DomainEvent> applicationEvent : applicationEvents) {
                interceptor.process(applicationEvent);
            }
        }
        log.debug("Persisting entity {} at version: {}", entity.identity(), entity.version());
        final Version version = eventStore.append(identifierFromEntity.streamIdentifierOf(entityClass, entity.identity()), applicationEvents, entity.version());
        log.debug("Updated entity {} at version: {}", entity.identity(), version);
        entity.updateTo(version);
    }

    public static Function<DomainEvent, ApplicationEvent<DomainEvent>> toApplicationEvent(final Map<String, Object> headers) {
        return new Function<DomainEvent, ApplicationEvent<DomainEvent>>() {
            @Override
            public ApplicationEvent<DomainEvent> apply(DomainEvent input) {
                return ApplicationEvent.of(input, headers);
            }
        };
    }

    @Override
    public boolean contains(ID domainIdentity) {
        return eventStore.eventStream(identifierFromEntity.streamIdentifierOf(entityClass, domainIdentity)).version().isGreater(Version.UNINITIALIZED);
    }

    private E create(Class<E> clazz, DomainEntityState<ID> state) {

        try {
            final Constructor<E> constr = clazz.getDeclaredConstructor(state.getClass());

            constr.setAccessible(true);

            return constr.newInstance(state);
        } 
        catch (Exception e) {
            throw new RuntimeException(String.format("Class %s should have a constructor:\nprotected %s(%s state);{\n\tsuper(state)\n}", clazz.getCanonicalName(), clazz.getSimpleName(), state.getClass().getSimpleName()), e);
        }
    }
}
