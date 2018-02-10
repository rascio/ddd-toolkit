package it.r.dddtoolkit.cqrs.command.adapter.simple;

import com.google.common.base.Preconditions;
import it.r.dddtoolkit.core.Context;
import it.r.dddtoolkit.cqrs.command.AggregateCommandHandler;
import it.r.dddtoolkit.ddd.AggregateRepository;
import it.r.dddtoolkit.es.ddd.TickCommand;
import it.r.dddtoolkit.es.ddd.TickEvent;
import it.r.dddtoolkit.es.ddd.TickTock;

import java.util.function.Function;

public class TickCommandHandler extends AggregateCommandHandler<TickTock, TickCommand, Context> {

    public TickCommandHandler(AggregateRepository<TickTock, Context> repository) {
        super(repository);
    }

    @Override
    protected void handle(TickTock aggregate, TickCommand command, Context context) {
        Preconditions.checkState(!aggregate.state().isTick(),
            "It's 'tock' time!");

        aggregate.apply(new TickEvent());
    }
}
