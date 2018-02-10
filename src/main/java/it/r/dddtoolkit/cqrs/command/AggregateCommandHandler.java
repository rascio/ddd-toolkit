package it.r.dddtoolkit.cqrs.command;

import it.r.dddtoolkit.core.Context;
import it.r.dddtoolkit.ddd.AggregateRepository;
import it.r.dddtoolkit.modules.es.ddd.EventSourcedAggregate;
import it.r.dddtoolkit.modules.es.ddd.EventSourcedAggregateRepository;

/**
 * Created by rascio on 04/02/18.
 */
public abstract class AggregateCommandHandler<A extends EventSourcedAggregate<?, CTX>, C extends Command, CTX extends Context> implements CommandHandler<C, CTX> {

    private final AggregateRepository<A, CTX> repository;

    protected AggregateCommandHandler(AggregateRepository<A, CTX> repository) {
        this.repository = repository;
    }

    @Override
    public void handle(C command, CTX context) {

        final A aggregate = repository.findByIdentity(context);

        handle(aggregate, command, context);

        repository.store(aggregate);
    }

    protected abstract void handle(A aggregate, C command, CTX context);
}
