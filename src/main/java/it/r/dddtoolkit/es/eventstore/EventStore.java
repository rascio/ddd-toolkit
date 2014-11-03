package it.r.dddtoolkit.es.eventstore;

import it.r.dddtoolkit.ddd.DomainEvent;
import it.r.dddtoolkit.es.ApplicationEvent;
import it.r.dddtoolkit.es.eventstore.EventStream.Version;

import java.util.List;

/**
 * Store di eventi
 * @author rascioni
 */
public interface EventStore {

	/**
	 * Restituisce lo stream di eventi identificato dal parametro passato.
	 * Nel caso di uno stream assente, verrà restituito uno stream vuoto.
	 * @param streamId
	 * @return 
	 */
    EventStream eventStream (String streamId);
	/**
	 * Appende degli eventi ad uno stream e ne modifica la versione, restituendola.
	 * Nel caso si voglia inizializzare uno stream non esistente l'<code>expectedVersion</code> da passare sarà {@link Version#UNINITIALIZED}.
	 * @param streamIdentifier
	 * @param events
	 * @param expectedVersion
	 * @return 
	 */
    Version append(String streamIdentifier, List<ApplicationEvent<DomainEvent>> events, Version expectedVersion);
    
	/**
	 * Restituisce la lista di tutti gli eventi salvati.
	 * @return 
	 */
    List<ApplicationEvent<DomainEvent>> history();

}
