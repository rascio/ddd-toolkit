package it.r.dddtoolkit.modules.es.ddd;

import it.r.dddtoolkit.ddd.AggregateRepository;
import it.r.dddtoolkit.core.Context;
import it.r.dddtoolkit.modules.es.eventstore.EventStore;
import it.r.dddtoolkit.modules.es.eventstore.EventStream;
import it.r.dddtoolkit.modules.es.eventstore.Version;
import lombok.extern.slf4j.Slf4j;

import java.util.function.Function;

/**
 * Un {@link EventSourcedAggregateRepository} viene utilizzato durante la gestione di un comando.
 * @param <A>
 * @param <C>
 */
@Slf4j
public class EventSourcedAggregateRepository<A extends EventSourcedAggregate<?, C>, C extends Context> implements AggregateRepository<A> {

    private final EventStore eventStore;
    private final Class<A> entityClass;
    private final C context;

    public static <A extends EventSourcedAggregate<?, C>, C extends Context> Function<C, AggregateRepository<A>> factoryFrom(EventStore<C> eventStore, Class<A> aggregateType) {
        return ctx -> new EventSourcedAggregateRepository<>(eventStore, aggregateType, ctx);
    }

    public EventSourcedAggregateRepository(EventStore eventStore, Class<A> entityClass, C context) {
        this.eventStore = eventStore;
        this.entityClass = entityClass;
        this.context = context;
    }

    public EventSourcedAggregateRepository(EventStore eventStore, Class<A> entityClass) {
        this(eventStore, entityClass, null);
    }

    @Override
    public A findByIdentity(String aggregateId) {

        final A aggregate;
        try {
            aggregate = entityClass.getConstructor(String.class)
                .newInstance(aggregateId);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(String.format(
                "Missing constructor: \npublic %s (String aggregateId) {\n\tsuper(aggregateId);\n}", entityClass.getSimpleName()));
        }
        catch (Exception e) {
            throw new RuntimeException("BOOM!", e);
        }

        final EventStream<C> eventStream = eventStore.eventStream(aggregateId);

        if (eventStream.hasTransactions()) {
            aggregate.load(eventStream);
        }

        return aggregate;
    }

    @Override
    public void store(A entity) {

        log.debug("Persisting entity {} at version: {}", entity.identity(), entity.version());

        entity.commit(events -> {
            final AggregateTransaction<C> transaction = new AggregateTransaction<>(
                entity.identity(),
                events,
                context
            );
            final Version version = eventStore.append(transaction, entity.version());
            log.debug("Updated entity {} at version: {}", entity.identity(), version);

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
