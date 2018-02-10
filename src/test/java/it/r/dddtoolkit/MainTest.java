package it.r.dddtoolkit;

import it.r.dddtoolkit.core.Context;
import it.r.dddtoolkit.cqrs.command.CommandBus;
import it.r.dddtoolkit.cqrs.command.adapter.simple.MapCommandBus;
import it.r.dddtoolkit.cqrs.command.adapter.simple.TickCommandHandler;
import it.r.dddtoolkit.cqrs.command.adapter.simple.TockCommandHandler;
import it.r.dddtoolkit.es.ddd.TickCommand;
import it.r.dddtoolkit.es.ddd.TickTock;
import it.r.dddtoolkit.es.ddd.TockCommand;
import it.r.dddtoolkit.modules.es.ddd.EventSourcedAggregateRepository;
import it.r.dddtoolkit.modules.es.eventstore.EventStore;
import it.r.dddtoolkit.modules.es.eventstore.EventStream;
import it.r.dddtoolkit.modules.es.eventstore.Version;
import it.r.dddtoolkit.modules.es.eventstore.memorydb.MemoryEventStore;
import org.junit.Assert;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class MainTest {

    @Test
    public void test() {
        final EventStore<Context> eventStore = new MemoryEventStore<>();

        final EventSourcedAggregateRepository<TickTock, Context> repository = new EventSourcedAggregateRepository<>(eventStore, TickTock.class);
        final CommandBus<Context> commandBus = MapCommandBus.builder()
            .register(TickCommand.class, new TickCommandHandler(repository))
            .register(TockCommand.class, new TockCommandHandler(repository))
            .build();

        commandBus.handle(new TickCommand(), () -> "test");
        commandBus.handle(new TockCommand(), () -> "test");
        try {
            commandBus.handle(new TockCommand(), () -> "test");

            fail("you can't 'tock' two times!");
        }
        catch (Exception e) { }

        commandBus.handle(new TickCommand(), () -> "test2");
        commandBus.handle(new TickCommand(), () -> "test");

        final EventStream<Context> history = eventStore.happenedFrom(Version.UNINITIALIZED);

        history.transactions()
            .peek(e -> System.out.println(e.getKey() + " - " + e.getValue().getStreamId()))
            .flatMap(e -> e.getValue().getEvents().stream())
            .forEach(e -> System.out.println("\t" + e));

        assertEquals(4, history.events().count());


        final EventStream<Context> latest = eventStore.happenedFrom(history.lastVersion());

        assertEquals(0, latest.events().count());
    }
}
