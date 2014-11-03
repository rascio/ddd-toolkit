package it.r.dddtoolkit.es.eventstore.mongodb;

import it.r.dddtoolkit.ddd.DomainEvent;
import it.r.dddtoolkit.es.ApplicationEvent;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import org.bson.types.ObjectId;
import org.mongojack.Id;

import com.fasterxml.jackson.annotation.JsonProperty;

@AllArgsConstructor
@NoArgsConstructor
@Data
class MongoDbEventDescriptor<ID> {
	
	@Id
	@JsonProperty("_id")
	private ObjectId id;

	private ApplicationEvent<DomainEvent> event;
	
	private String streamIdentity;
	
	private Long version;

}
