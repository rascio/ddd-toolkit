package it.r.dddtoolkit.es.support;

import java.io.Serializable;

public interface EventStreamIdentifierFromEntity<ID extends Serializable> {

	String streamIdentifierOf(Class<?> entityType, ID entityIdentifier);
}
