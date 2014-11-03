package it.r.dddtoolkit.es.ddd;

import it.r.dddtoolkit.ddd.DomainEvent;
import it.r.dddtoolkit.es.eventstore.EventStream.Version;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.AccessLevel;
import lombok.Setter;

/**
 * Classe base per lo stato di un entità del dominio con funzionalità di event
 * sourcing. Una classe che implementa DomainEntityState dovrà definire
 * OBBLIGATORIAMENTE un costruttore che prenda in ingresso l'identità
 * dell'entity corrispondente. Inoltre la classe dovrà trovarsi nello stesso
 * package dell'entità ed avere una naming convention ${nomeEntita}Class.
 *
 * La classe stato dell'entità dovrà anche definire dei metodi
 * `when(DomainEvent)` per ogni domain event generato dalla rispettiva entità.
 *
 * @author rascioni
 * @param <ID>
 */
public abstract class DomainEntityState<ID> {

    private static final String MUTATOR_METHOD_NAME = "when";

    private static Map<String, Method> mutatorMethods = new HashMap<String, Method>();

    private Version version;

    @Setter(AccessLevel.PACKAGE)
    private ID identity;

    public DomainEntityState(ID id, Version v) {
        this.identity = id;
        this.version = v;
    }

    protected DomainEntityState(ID id) {
        this(id, Version.UNINITIALIZED);
    }

    protected DomainEntityState() {
        this(null);
    }

    public ID identity() {
        return identity;
    }

    public Version version() {
        return this.version;
    }

    void updateTo(Version v) {
        this.version = v;
    }

    void loadFromHistory(List<DomainEvent> events, Version version) {

        for (DomainEvent event : events) {
            this.mutate(event);
        }

        this.version = version;
    }

    /*
     *  PLUMBING, in java we dont have @dynamic
     */
    protected void mutate(DomainEvent aDomainEvent) {

        Class<? extends DomainEntityState> rootType = this.getClass();

        Class<? extends DomainEvent> eventType = aDomainEvent.getClass();

        String key = rootType.getName() + ":" + eventType.getName();

        Method mutatorMethod = mutatorMethod(key, rootType, eventType);

        try {
            mutatorMethod.invoke(this, aDomainEvent);

        } catch (Exception e) {

            throw new RuntimeException(String.format("when %s on %s there was an error", aDomainEvent, this.getClass().getSimpleName()), e);

        }
    }

    private Method mutatorMethod(
            String aKey,
            Class<? extends DomainEntityState> aRootType,
            Class<? extends DomainEvent> anEventType) {

        Method method = mutatorMethods.get(aKey);

        if (method != null) {
            return method;
        }

        synchronized (mutatorMethods) {
            try {
                method = aRootType.getDeclaredMethod(
                        MUTATOR_METHOD_NAME,
                        anEventType);

                method.setAccessible(true);

                mutatorMethods.put(aKey, method);

                return method;

            } catch (Exception e) {
                throw new IllegalArgumentException(
                        "I do not understand "
                        + MUTATOR_METHOD_NAME
                        + "("
                        + anEventType.getSimpleName()
                        + ") because: "
                        + e.getClass().getSimpleName() + ">>>" + e.getMessage(),
                        e);
            }
        }
    }
}
