package it.r.dddtoolkit.modules.es.eventstore.mongodb;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import it.r.dddtoolkit.core.DomainEvent;
import it.r.dddtoolkit.core.Context;
import it.r.dddtoolkit.modules.es.eventstore.Version;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import org.bson.types.ObjectId;
import org.mongojack.Id;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
class TransactionCommitDocument<C extends Context> {

	@Id
	@JsonProperty("_id")
	private ObjectId id;

	private String eventStreamId;

	private Version version;

	@JsonTypeInfo(use= JsonTypeInfo.Id.CLASS, include=JsonTypeInfo.As.PROPERTY, property="type")
	private List<DomainEvent> events;

	private C context;

}
