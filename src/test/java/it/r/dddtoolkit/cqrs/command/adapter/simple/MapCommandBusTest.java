package it.r.dddtoolkit.cqrs.command.adapter.simple;

import it.r.dddtoolkit.cqrs.command.CommandBus;
import it.r.dddtoolkit.core.Context;
import it.r.dddtoolkit.es.ddd.TickCommand;
import it.r.dddtoolkit.modules.es.ddd.EventSourcedAggregateRepository;
import it.r.dddtoolkit.es.ddd.TickTock;
import it.r.dddtoolkit.modules.es.eventstore.memorydb.MemoryEventStore;

public class MapCommandBusTest {

    public static void main(String[] args) {
        final CommandBus<Context> bus = MapCommandBus.builder()
            .register(TickCommand.class, new TickCommandHandler(new EventSourcedAggregateRepository<>(new MemoryEventStore(), TickTock.class)))
            .build();

        bus.handle(new TickCommand(), () -> "5");
        bus.handle(new TickCommand(), () -> "5");
    }

}