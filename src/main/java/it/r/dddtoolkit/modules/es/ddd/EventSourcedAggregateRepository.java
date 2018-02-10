package it.r.dddtoolkit.modules.es.ddd;

import it.r.dddtoolkit.ddd.AggregateRepository;
import it.r.dddtoolkit.core.Context;
import it.r.dddtoolkit.modules.es.eventstore.EventStore;
import it.r.dddtoolkit.modules.es.eventstore.EventStream;
import it.r.dddtoolkit.modules.es.eventstore.Version;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Un {@link EventSourcedAggregateRepository} viene utilizzato durante la gestione di un comando.
 * @param <A>
 * @param <C>
 */
@Slf4j
public class EventSourcedAggregateRepository<A extends EventSourcedAggregate<?, C>, C extends Context> implements AggregateRepository<A, C> {

    private final EventStore eventStore;
    private final Class<A> entityClass;

    public EventSourcedAggregateRepository(EventStore eventStore, Class<A> entityClass) {
        this.eventStore = eventStore;
        this.entityClass = entityClass;
    }

    @Override
    public A findByIdentity(C context) {

        final A aggregate;
        try {
            aggregate = Stream.of(entityClass.getConstructors())
                .filter(c -> c.getParameterTypes().length == 1)
                .filter(c -> c.getParameterTypes()[0].isAssignableFrom(context.getClass()))
                .map(c -> (Constructor<A>) c)
                .findFirst()
                .orElseThrow(() -> new RuntimeException(String.format(
                    "Missing constructor: \npublic %s (%s context) {\n\tsuper(context);\n}", entityClass.getSimpleName(), context.getClass().getSimpleName()))
                )
                .newInstance(context);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException("BOOM!!!", e);
        }

        final EventStream<C> eventStream = eventStore.eventStream(context.getAggregateId());

        log.trace("Loading from stream {}@{}", aggregate.identity(), aggregate.version().getNumber());
        if (eventStream.hasTransactions()) {
            aggregate.load(eventStream);
        }
        log.trace("Aggregate {} loaded", aggregate.identity());
        return aggregate;
    }

    @Override
    public void store(A entity) {

        log.trace("Persisting entity {} at version: {}", entity.identity(), entity.version());

        entity.commit(events -> {
            final EventsTransaction<C> transaction = new EventsTransaction<>(
                entity.identity(),
                events,
                entity.getContext()
            );
            final Version version = eventStore.append(transaction, entity.version());
            log.trace("Updated entity {} at version: {}", entity.identity(), version);

            return version;
        });


        // TODO: REFACTOR IN A SEPARATE PROCESS
//		for (DomainEvent event : transaction.getEvents()) {
//
//            log.trace("Publishing event: {}", event);
//            eventPublisher.publish(event, context);
//        }
        // *******************************
    }
}
