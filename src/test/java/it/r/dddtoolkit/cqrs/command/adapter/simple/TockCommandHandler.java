package it.r.dddtoolkit.cqrs.command.adapter.simple;

import com.google.common.base.Preconditions;
import it.r.dddtoolkit.core.Context;
import it.r.dddtoolkit.cqrs.command.AggregateCommandHandler;
import it.r.dddtoolkit.ddd.AggregateRepository;
import it.r.dddtoolkit.es.ddd.*;
import it.r.dddtoolkit.modules.es.ddd.EventSourcedAggregateRepository;

public class TockCommandHandler extends AggregateCommandHandler<TickTock, TockCommand, Context> {

    public TockCommandHandler(AggregateRepository<TickTock, Context> repository) {
        super(repository);
    }

    @Override
    protected void handle(TickTock aggregate, TockCommand command, Context context) {
        Preconditions.checkState(aggregate.state().isTick(),
            "It's 'tock' time!");

        aggregate.apply(new TockEvent());
    }
}
