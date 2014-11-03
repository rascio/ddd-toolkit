package it.r.dddtoolkit.es.ddd;

import it.r.dddtoolkit.ddd.DomainEvent;
import it.r.dddtoolkit.es.ApplicationEvent;

public interface PreStoreEventInterceptor {

	void process(ApplicationEvent<DomainEvent> event);
}
