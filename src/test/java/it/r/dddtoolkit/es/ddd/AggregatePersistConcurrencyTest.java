package it.r.dddtoolkit.es.ddd;

import static org.junit.Assert.fail;

import it.r.dddtoolkit.cqrs.command.CommandBus;
import it.r.dddtoolkit.cqrs.command.adapter.simple.MapCommandBus;
import it.r.dddtoolkit.cqrs.command.adapter.simple.TickCommandHandler;
import it.r.dddtoolkit.cqrs.command.adapter.simple.TockCommandHandler;
import it.r.dddtoolkit.ddd.AggregateRepository;
import it.r.dddtoolkit.core.Context;
import it.r.dddtoolkit.modules.es.ddd.EventSourcedAggregate;
import it.r.dddtoolkit.modules.es.ddd.EventSourcedAggregateRepository;
import it.r.dddtoolkit.modules.es.eventstore.EventStore;
import it.r.dddtoolkit.modules.es.eventstore.memorydb.MemoryEventStore;

import lombok.val;
import org.junit.Before;
import org.junit.Test;

import java.util.function.Function;

public class AggregatePersistConcurrencyTest {

	private EventStore eventStore;

	@Before
	public void setUp(){
		eventStore = new MemoryEventStore();
	}

	@Test
	public void test() {

        final EventSourcedAggregateRepository<TickTock, Context> repository = new EventSourcedAggregateRepository<>(
		    eventStore,
		    TickTock.class,
            () -> "test"
        );

        final TickTock tickTock = new TickTock("test");

		tickTock.apply(new TickEvent());
		repository.store(tickTock);

		final TickTock tickTock2 = repository.findByIdentity(tickTock.identity());

		tickTock2.apply(new TockEvent());
		repository.store(tickTock2); //increment the version on tickTock2

        try {
            tickTock.apply(new TockEvent());
            tickTock.apply(new TickEvent());

            repository.store(tickTock); //tickTock has an old version

            fail("An exception should be thrown!");
        }
		catch (IllegalStateException e) {
		}

        tickTock2.apply(new TockEvent());
        tickTock2.apply(new TickEvent());
		
		repository.store(tickTock2);
	}

}
