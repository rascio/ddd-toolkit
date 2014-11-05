package it.r.dddtoolkit.es.eventstore;

import it.r.dddtoolkit.ddd.DomainEvent;
import it.r.dddtoolkit.es.ApplicationEvent;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Value;
import lombok.experimental.Accessors;

import com.google.common.collect.Iterables;
import java.util.LinkedList;

@Value
@Accessors(fluent = true)
@AllArgsConstructor
public class EventStream {
	
	public EventStream() {
		this(new LinkedList<ApplicationEvent<DomainEvent>>(), Version.UNINITIALIZED);
	}

    Iterable<ApplicationEvent<DomainEvent>> events;
    Version version;

    public EventStream append(List<ApplicationEvent<DomainEvent>> events) {
        return new EventStream(Iterables.concat(this.events, events), this.version.next());
    }

    @Value
    @AllArgsConstructor(staticName = "of")
    public static class Version {

        public static Version UNINITIALIZED = Version.of(-1);

        private long number;

        public Version next() {
            return Version.of(System.currentTimeMillis());
        }
    }
}
