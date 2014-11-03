//package it.r.dddtoolkit.es.eventstore.jpa;
//
//import it.r.dddtoolkit.es.ApplicationEvent;
//import it.r.dddtoolkit.es.eventstore.EventStore;
//import it.r.dddtoolkit.es.eventstore.EventStream;
//
//import java.util.ArrayList;
//import java.util.List;
//
//import javax.persistence.EntityManager;
//
//import lombok.AllArgsConstructor;
//import lombok.SneakyThrows;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//
//@AllArgsConstructor
//public class JpaEventStore implements EventStore {
//
//	private final EntityManager entityManager;
//	private final ObjectMapper objectMapper;
//	@Override
//	public void save(EventStream<?> eventStream) {
//		
//		final JpaEventDescriptor<?> lastDescriptor = entityManager.createQuery("from JpaEventDescriptor order by created desc", JpaEventDescriptor.class).getSingleResult();
//		if (lastDescriptor.version >= eventStream.version()){
//			throw new RuntimeException("Concurrency Exception");
//		}
//		for (ApplicationEvent<?, ?> domainEvent : eventStream.events()){
//			entityManager.persist(new JpaEventDescriptor(eventStream.streamIdentity(), eventStream.version(), serialize(domainEvent)));
//		}
//
//	}
//
//	@SneakyThrows
//	private String serialize(ApplicationEvent<?, ?> domainEvent) {
//		return objectMapper.writeValueAsString(domainEvent);
//	}
//
//	@Override
//	public EventStream<?> eventStream(String streamId) {
//		final List<JpaEventDescriptor> descriptors = entityManager.createQuery("from JpaEventDescriptor e fetch e.event where e.created order by created asc", JpaEventDescriptor.class).getResultList();
//		final List<ApplicationEvent<?, ?>> events = new ArrayList<>(descriptors.size());
//		
//		int version = 0;
//		for (JpaEventDescriptor<?> descriptor : descriptors){
//			events.add(deserialize(descriptor.event));
//			version = descriptor.version;
//		}
//		return new EventStream(events, streamId, version);
//	}
//
//	@SneakyThrows
//	private ApplicationEvent<?, ?> deserialize(String event) {
//		return objectMapper.readValue(event, ApplicationEvent.class);
//	}
//
//}
