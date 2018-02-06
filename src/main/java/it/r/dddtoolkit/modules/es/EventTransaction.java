package it.r.dddtoolkit.modules.es;

import it.r.dddtoolkit.core.DomainEvent;
import it.r.dddtoolkit.core.Context;
import it.r.dddtoolkit.modules.es.eventstore.Version;
import lombok.Value;

import java.util.List;

/**
 * Rappresenta il risultato di una mutazione di un aggregato, è utilizzato per aggiungere
 * informazioni non relative al dominio. Un evento dell'applicazione contiene
 * gli headers (customizabili in base all'applicazione) e un evento del dominio.
 *
 * @author rascioni
 */
@Value
public class EventTransaction<C extends Context> {

    String aggregateId;
    Version version;
    List<DomainEvent> events;
    C context;

}
