package it.r.dddtoolkit.es.ddd;

import static com.google.common.collect.FluentIterable.from;
import it.r.dddtoolkit.ddd.DomainEvent;

import java.io.Serializable;
import java.util.List;

import com.google.common.base.Predicate;

public class EventSourcingHelper {

	public static <ID extends Serializable> List<DomainEvent> generatedEvents(EventSourcedDomainEntity<?, ID> entity) {
		return entity.mutatingEvents();
	}
	
	public static <ID extends Serializable> boolean was(Predicate<? super DomainEvent> predicate, EventSourcedDomainEntity<?, ID> entity) {
		return from(generatedEvents(entity)).anyMatch(predicate);
	}
}
