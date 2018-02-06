package it.r.dddtoolkit.modules.es.ddd;

import it.r.dddtoolkit.ddd.DomainRepository;
import it.r.dddtoolkit.modules.es.EventTransaction;
import it.r.dddtoolkit.core.Context;
import it.r.dddtoolkit.modules.es.eventstore.EventStore;
import it.r.dddtoolkit.modules.es.eventstore.EventStream;
import it.r.dddtoolkit.modules.es.eventstore.Version;
import lombok.extern.slf4j.Slf4j;

/**
 * Implementazione generica di un {@link DomainRepository} per salvare {@link EventSourcedAggregate} in un {@link EventStore}.
 * @author rascioni
 * @param <E>
 * @param <S>
 */
@Slf4j
public class EventSourcingDomainRepository<E extends EventSourcedAggregate<S, C>, S, C extends Context> implements DomainRepository<E> {

    private final EventStore eventStore;
    private final Class<E> entityClass;
    private final C context;

    public EventSourcingDomainRepository(EventStore eventStore, Class<E> entityClass, C context) {
        this.eventStore = eventStore;
        this.entityClass = entityClass;
        this.context = context;
    }

    public EventSourcingDomainRepository(EventStore eventStore, Class<E> entityClass) {
        this(eventStore, entityClass, null);
    }

    @Override
    public E findByIdentity(String aggregateId) {

        final E aggregate;
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
    public void store(E entity) {

        log.debug("Persisting entity {} at version: {}", entity.identity(), entity.version());

        final EventTransaction<C> transaction = entity.commit(context);
        final Version version = eventStore.append(transaction, entity.version());
        log.debug("Updated entity {} at version: {}", entity.identity(), version);


        // TODO: REFACTOR IN A SEPARATE PROCESS
//		for (DomainEvent event : transaction.getEvents()) {
//
//            log.trace("Publishing event: {}", event);
//            eventPublisher.publish(event, context);
//        }
        // *******************************
    }
}
