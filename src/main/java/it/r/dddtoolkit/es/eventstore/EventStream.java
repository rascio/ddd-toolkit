package it.r.dddtoolkit.es.eventstore;

import it.r.dddtoolkit.ddd.DomainEvent;
import it.r.dddtoolkit.es.ApplicationEvent;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.Value;
import lombok.experimental.Accessors;

import com.google.common.collect.Iterables;

@Value
@Accessors(fluent = true)
public class EventStream {

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

        public boolean isGreater(@NonNull Version v) {
            return this.number > v.number;
        }
    }
}
