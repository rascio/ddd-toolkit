package it.r.dddtoolkit.es.eventstore;

import it.r.dddtoolkit.ddd.DomainEvent;
import it.r.dddtoolkit.es.ApplicationEvent;
import it.r.dddtoolkit.es.eventstore.EventStream.Version;

import java.util.List;


public interface EventStore {

    EventStream eventStream (String streamId);

    Version append(String streamIdentifier, List<ApplicationEvent<DomainEvent>> events, Version expectedVersion);
    
    List<ApplicationEvent<DomainEvent>> history();

}
