package it.r.dddtoolkit.modules.es.support;

import java.io.Serializable;

public final class DefaultEventStreamIdentifierFromEntity<ID extends Serializable> implements EventStreamIdentifierFromEntity<ID> {
	@Override
	public String streamIdentifierOf(Class<?> entityType, ID entityIdentifier) {
		return String.format("%s.%s", entityType.getName(), entityIdentifier.hashCode());
	}
}