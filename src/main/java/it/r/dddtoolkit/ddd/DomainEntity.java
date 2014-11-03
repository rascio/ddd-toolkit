package it.r.dddtoolkit.ddd;

import java.io.Serializable;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * Classe rappresentante un'entità del dominio. Un'entità è un concetto del
 * dominio il quale può essere definito da una sua identità, piuttosto che dai
 * suoi attributi.
 *
 * @author rascioni
 * @param <ID>
 */
public abstract class DomainEntity<ID extends Serializable> {

    /**
     * Restituisce l'identità
     *
     * @return identity
     */
    public abstract ID identity();

    /**
     * Verifica se {@code other} ha la stessa identità di questa instanza.
     *
     * @param other da paragonare
     * @return {@code true} se la condizione e' verificata, altrimenti
     * {@code false}
     */
    public boolean sameIdentityAs(DomainEntity<ID> other) {
        if (other == null || !other.getClass().equals(this.getClass())) {
            return false;
        }
        ID identity = identity();
        ID otherIdentity = other.identity();
        return identity.equals(otherIdentity);
    }

    /**
     * Costruttore vuoto obbligatorio per poter instanziare l'entity tramite
     * reflection
     */
    protected DomainEntity() {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final int hashCode() {
        return identity().hashCode();
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
        return sameIdentityAs((DomainEntity<ID>) object);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }

}
