package it.r.dddtoolkit.es.support.guava;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import it.r.dddtoolkit.ddd.DomainEvent;
import it.r.dddtoolkit.es.ApplicationEvent;
import it.r.dddtoolkit.es.support.EventPublisher.EventCallback;

import org.junit.Test;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableMap;

public class GuavaEventPublisherTest {

	@Test
	public void testPublishSubscribeWithSuperClass() {
		final GuavaEventPublisher guavaEventPublisher = GuavaEventPublisher.sync();
		
		final Listener listener = new Listener();
		
		guavaEventPublisher.subscribe(DomainEvent.class, listener);
		
		final MyDomainEvent event = new MyDomainEvent();
		
		guavaEventPublisher.publish(ApplicationEvent.of(event, ImmutableMap.<String, Object>of(ApplicationEvent.AGGREGATE_ID, "test")));
		
		assertTrue(listener.received.isPresent());
		assertEquals(listener.getReceived().get(), event);
		
	}

	
	private static class MyDomainEvent implements DomainEvent {
		
	}
	
	public static class Listener implements EventCallback<DomainEvent>{
		private Optional<DomainEvent> received = Optional.absent();
		@Override
		public void on(ApplicationEvent<DomainEvent> event) {
			this.received = Optional.of(event.getEvent());
		}
		
		public Optional<DomainEvent> getReceived() {
			return received;
		}
	}
}
