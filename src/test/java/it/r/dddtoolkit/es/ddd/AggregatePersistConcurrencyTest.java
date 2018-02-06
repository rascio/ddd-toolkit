package it.r.dddtoolkit.es.ddd;

import static org.junit.Assert.fail;
import it.r.dddtoolkit.ddd.DomainRepository;
import it.r.dddtoolkit.core.Context;
import it.r.dddtoolkit.modules.es.ddd.EventSourcingDomainRepository;
import it.r.dddtoolkit.modules.es.eventstore.EventStore;
import it.r.dddtoolkit.modules.es.eventstore.memorydb.MemoryEventStore;
import it.r.dddtoolkit.modules.es.support.EventPublisher;

import org.junit.Before;
import org.junit.Test;

import it.r.dddtoolkit.core.DomainEvent;
import it.r.dddtoolkit.modules.es.support.Subscription;

public class AggregatePersistConcurrencyTest {
	
	private EventPublisher eventPublisher;
	private EventStore eventStore;
	private DomainRepository<TickTock> repository;
	
	@Before
	public void setUp(){
		
		eventPublisher = new EventPublisher() {
			@Override
			public Subscription subscribe(Class eventType, EventCallback callback) {
				return null;
			}

			@Override
			public void publish(DomainEvent event, Context context) {

			}
		};
		
		eventStore = new MemoryEventStore();
		
		repository = new EventSourcingDomainRepository(eventStore, TickTock.class, () -> "1");
	}

	@Test
	public void test() {
		
		final TickTock tickTock = new TickTock("test");
		
		tickTock.tick();
		
		repository.store(tickTock);
		
		final TickTock tickTock2 = repository.findByIdentity(tickTock.identity());
		
		tickTock2.tock();
		repository.store(tickTock2);
		
		tickTock.tock();
		tickTock.tick();
		Exception e = null;
		try {
			repository.store(tickTock);
		}
		catch (IllegalStateException e1) {
			e = e1;
		}
		if (e == null){
			fail("It missed the concurrent exception!");
		}

		tickTock2.tick();
		tickTock2.tock();
		
		repository.store(tickTock2);
	}

}
