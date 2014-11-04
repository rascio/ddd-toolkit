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
import it.r.dddtoolkit.ddd.DomainEvent;
import it.r.dddtoolkit.es.ApplicationEvent;
import it.r.dddtoolkit.es.support.Subscription;

public class DomainEntityPersistConcurrency {
	
	private EventPublisher eventPublisher;
	private EventStore eventStore;
	private DomainRepository<TickTock, String> repository;
	
	@Before
	public void setUp(){
		
		eventPublisher = new MutedEventPublisher();
		
		eventStore = new MemoryEventStore();
		
		repository = new EventSourcingDomainRepository(eventStore, TickTock.class, ImmutableList.<PreStoreEventInterceptor>of(), eventPublisher);
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
	
	private static class MutedEventPublisher implements EventPublisher {

		@Override
		public <D extends DomainEvent> Subscription subscribe(Class<D> eventType, EventCallback<D> callback) {
			return null;
		}

		@Override
		public void publish(ApplicationEvent<?> event) {
		}
		
	}

}
