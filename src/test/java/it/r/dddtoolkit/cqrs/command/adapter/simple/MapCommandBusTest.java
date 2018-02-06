package it.r.dddtoolkit.cqrs.command.adapter.simple;

import com.google.common.base.Preconditions;
import it.r.dddtoolkit.cqrs.command.AggregateCommandHandler;
import it.r.dddtoolkit.cqrs.command.Command;
import it.r.dddtoolkit.cqrs.command.CommandBus;
import it.r.dddtoolkit.ddd.DomainRepository;
import it.r.dddtoolkit.core.Context;
import it.r.dddtoolkit.modules.es.ddd.EventSourcingDomainRepository;
import it.r.dddtoolkit.es.ddd.TickEvent;
import it.r.dddtoolkit.es.ddd.TickTock;
import it.r.dddtoolkit.modules.es.eventstore.memorydb.MemoryEventStore;

public class MapCommandBusTest {

    public static void main(String[] args) {
        final CommandBus<Context> bus = MapCommandBus.builder()
            .register(TickCommand.class, new TickCommandHandler(new EventSourcingDomainRepository<>(new MemoryEventStore(), TickTock.class)))
            .build();

        bus.handle(new TickCommand(), () -> "5");
        bus.handle(new TickCommand(), () -> "5");
    }

    public static class TickCommand implements Command {

    }

    public static class TickCommandHandler extends AggregateCommandHandler<TickTock, TickCommand, Context> {

        protected TickCommandHandler(DomainRepository<TickTock> repository) {
            super(repository);
        }

        @Override
        protected void handle(TickTock aggregate, TickCommand command, Context context) {
            Preconditions.checkState(!aggregate.state().isTick(),
                "It's 'tock' time!");

            aggregate.apply(new TickEvent());
        }
    }

}