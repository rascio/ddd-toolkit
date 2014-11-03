package it.r.dddtoolkit.es.support.reactor;

import static reactor.event.selector.Selectors.T;
import it.r.dddtoolkit.ddd.DomainEvent;
import it.r.dddtoolkit.es.ApplicationEvent;
import it.r.dddtoolkit.es.support.EventPublisher;
import it.r.dddtoolkit.es.support.Subscription;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import reactor.core.Reactor;
import reactor.core.composable.Stream;
import reactor.event.Event;
import reactor.event.registry.Registration;
import reactor.function.Consumer;

@RequiredArgsConstructor
public class ReactorEventPublisher implements EventPublisher{

	private final Reactor reactor;
	
	@Override
	public void publish(ApplicationEvent<?> appEvent) {
		reactor.notify(identifier(appEvent), Event.<ApplicationEvent<?>>wrap(appEvent));
	}

	private Object identifier(final ApplicationEvent<?> event) {
		return event.getEvent().getClass();
	}
	
	public <D extends DomainEvent, ID> Registration<Consumer<Event<ApplicationEvent<D>>>> register(Consumer<Event<ApplicationEvent<D>>> consumer, Class<D> event) {
		return reactor.on(T(event), consumer);
	}
	
	@Override
	public <D extends DomainEvent> Subscription subscribe(@NonNull Class<D> eventType, @NonNull EventCallback<D> callback) {
		
		final Registration<Consumer<Event<ApplicationEvent<D>>>> registration = register(new DomainEventConsumer<D>(callback), eventType);
		return new Subscription() {
			
			@Override
			public void cancel() {
				registration.cancel();
			}
			@Override
			public void cancelAfterUse() {
				registration.cancelAfterUse();
			}
		};
	}
	
	@RequiredArgsConstructor
	private static class DomainEventConsumer<D extends DomainEvent> implements Consumer<Event<ApplicationEvent<D>>>{
		private final EventCallback<D> callback;
		
		@Override
		public void accept(Event<ApplicationEvent<D>> t) {
			callback.on(t.getData());
		}
		
	}
}
