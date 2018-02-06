package it.r.dddtoolkit.ddd;

import it.r.dddtoolkit.modules.es.eventstore.Version;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * Classe rappresentante un'entità del dominio. Un'entità è un concetto del
 * dominio il quale può essere definito da una sua identità, piuttosto che dai
 * suoi attributi.
 *
 * @author rascioni
 */
public abstract class Aggregate<S> {

    private final String id;
    protected Version version;
    protected S state;

    /**
     * Costruttore vuoto obbligatorio per poter instanziare l'entity tramite
     * reflection
     * @param id
     */
    protected Aggregate(String id, S state) {
        this.id = id;
        this.version = Version.UNINITIALIZED;
        this.state = state;
    }

    /**
     * Restituisce l'identità
     *
     * @return identity
     */
    public String identity() {
        return this.id;
    }

    public Version version() {
        return this.version;
    }

    public S state() {
        return this.state;
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
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }

}
