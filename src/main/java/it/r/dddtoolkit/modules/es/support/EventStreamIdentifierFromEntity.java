package it.r.dddtoolkit.modules.es.support;

import java.io.Serializable;

public interface EventStreamIdentifierFromEntity<ID extends Serializable> {

	String streamIdentifierOf(Class<?> entityType, ID entityIdentifier);
}
