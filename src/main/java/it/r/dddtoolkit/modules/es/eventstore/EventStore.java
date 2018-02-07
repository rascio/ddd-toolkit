package it.r.dddtoolkit.modules.es.eventstore;

import it.r.dddtoolkit.modules.es.ddd.AggregateTransaction;
import it.r.dddtoolkit.core.Context;

/**
 * Store di eventi
 * @author rascioni
 */
public interface EventStore<C extends Context> {

	/**
	 * Restituisce lo stream di eventi identificato dal parametro passato.
	 * Nel caso di uno stream assente, verrà restituito uno stream vuoto.
	 * @param streamId
	 * @return 
	 */
    EventStream<C> eventStream(String streamId);
	/**
	 * Appende degli eventi ad uno stream e ne modifica la versione, restituendola.
	 * Nel caso si voglia inizializzare uno stream non esistente l'<code>expectedVersion</code> da passare sarà {@link Version#UNINITIALIZED}.
	 * @param events
	 * @param expectedVersion
	 * @return
	 */
    Version append(AggregateTransaction<C> events, Version expectedVersion);
    
	/**
	 * Restituisce la lista di tutti gli eventi salvati.
	 * @return 
	 */
    EventStream happenedFrom(Version version);

}
