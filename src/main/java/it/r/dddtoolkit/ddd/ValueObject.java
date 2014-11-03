package it.r.dddtoolkit.ddd;


import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.io.Serializable;

/**
 * Oggetto rappresentate un valore all'iterno del dominio.
 * Un valore è un concetto del dominio che non ha identità, ma sono importanti le sue caratteristiche.
 * @author rascioni
 * @param <VO> 
 */
public abstract class ValueObject<VO> implements Serializable {
	
	/**
	 * Verifica se due instanze hanno gli stessi valori.
	 * 
	 * @param other
	 *            l'altra instanza da paragonare
	 * @return {@code true} se la condizione e' verificata, {@code false}
	 *         altrimenti
	 */
	public final boolean sameValueAs(VO other) {
		return EqualsBuilder.reflectionEquals(this, other, false);
	}
	
	/**
	 * UID per {@link java.io.Serializable}.
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * Costruttore vuoto per la reflection.
	 */
	protected ValueObject() {
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public final int hashCode() {
		return HashCodeBuilder.reflectionHashCode(this);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	@SuppressWarnings("unchecked")
	public final boolean equals(Object object) {
		if (this == object) {
			return true;
		}
		if (object == null || getClass() != object.getClass()) {
			return false;
		}
		return sameValueAs((VO) object);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public final String toString() {
		return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
	}
	
}
