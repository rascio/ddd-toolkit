package it.r.dddtoolkit.es.eventstore.memorydb;


import it.r.dddtoolkit.es.ApplicationEvent;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@RequiredArgsConstructor
public class EventDescriptor {

    protected final int version;

    protected final long created = System.currentTimeMillis();

    protected final ApplicationEvent<?> event;


}
