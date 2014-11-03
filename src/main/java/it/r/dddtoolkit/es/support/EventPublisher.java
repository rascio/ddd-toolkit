package it.r.dddtoolkit.es.support;
import it.r.dddtoolkit.ddd.DomainEvent;
import it.r.dddtoolkit.es.ApplicationEvent;

public interface EventPublisher {
	
	public static interface EventCallback<D extends DomainEvent> {
		void on(ApplicationEvent<D> event);
	}

	<D extends DomainEvent> Subscription subscribe(Class<D> eventType, EventCallback<D> callback);

	void publish(ApplicationEvent<?> event);

}
