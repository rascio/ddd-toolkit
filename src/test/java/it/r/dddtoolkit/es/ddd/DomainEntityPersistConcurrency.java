package it.r.dddtoolkit.es.ddd;

import static org.junit.Assert.fail;
import it.r.dddtoolkit.ddd.DomainRepository;
import it.r.dddtoolkit.es.eventstore.EventStore;
import it.r.dddtoolkit.es.eventstore.memorydb.MemoryEventStore;
import it.r.dddtoolkit.es.support.EventPublisher;
import it.r.dddtoolkit.es.support.guava.GuavaEventPublisher;

import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.ImmutableList;

public class DomainEntityPersistConcurrency {
	
	private EventPublisher eventPublisher;
	private EventStore eventStore;
	private DomainRepository<TickTock, String> repository;
	
	@Before
	public void setUp(){
		
		eventPublisher = GuavaEventPublisher.sync();
		
		eventStore = new MemoryEventStore(eventPublisher);
		
		repository = new EventSourcingDomainRepository(eventStore, TickTock.class, ImmutableList.<PreStoreEventInterceptor>of());
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
