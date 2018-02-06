package it.r.dddtoolkit.modules.es.support;

import it.r.dddtoolkit.core.DomainEvent;
import it.r.dddtoolkit.core.Context;

public interface EventPublisher<C extends Context> {
	
	interface EventCallback<D extends DomainEvent> {
		void on(D event);
	}

	<D extends DomainEvent> Subscription subscribe(Class<D> eventType, EventCallback<D> callback);

	void publish(DomainEvent event, C context);

}
