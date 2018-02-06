package it.r.dddtoolkit.cqrs.command;

import it.r.dddtoolkit.ddd.Aggregate;
import it.r.dddtoolkit.ddd.DomainRepository;
import it.r.dddtoolkit.core.Context;

/**
 * Created by rascio on 04/02/18.
 */
public abstract class AggregateCommandHandler<A extends Aggregate<?>, C extends Command, CTX extends Context> implements CommandHandler<C, CTX> {

    private final DomainRepository<A> repository;

    protected AggregateCommandHandler(DomainRepository<A> repository) {
        this.repository = repository;
    }

    @Override
    public void handle(C command, CTX context) {
        final A aggregate = repository.findByIdentity(context.getAggregateId());

        handle(aggregate, command, context);

        repository.store(aggregate);
    }

    protected abstract void handle(A aggregate, C command, CTX context);
}
