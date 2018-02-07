package it.r.dddtoolkit.cqrs.command;

import it.r.dddtoolkit.ddd.Aggregate;
import it.r.dddtoolkit.core.Context;
import it.r.dddtoolkit.ddd.AggregateRepository;

import java.util.function.Function;

/**
 * Created by rascio on 04/02/18.
 */
public abstract class AggregateCommandHandler<A extends Aggregate<?>, C extends Command, CTX extends Context> implements CommandHandler<C, CTX> {

    private final Function<CTX, AggregateRepository<A>> factory;

    protected AggregateCommandHandler(Function<CTX, AggregateRepository<A>> factory) {
        this.factory = factory;
    }

    @Override
    public void handle(C command, CTX context) {
        final AggregateRepository<A> repository = factory.apply(context);

        final A aggregate = repository.findByIdentity(context.getAggregateId());

        handle(aggregate, command, context);

        repository.store(aggregate);
    }

    protected abstract void handle(A aggregate, C command, CTX context);
}
