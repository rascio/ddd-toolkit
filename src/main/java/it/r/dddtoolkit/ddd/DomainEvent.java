package it.r.dddtoolkit.ddd;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.As;

/**
 * Marker interface che rappresenta un'evento del dominio. Un evento di dominio
 * è ciò a cui è interessato il business, il risultato di un'operazione eseguita
 * dal sistema. Generalmente un evento ha un nome che esprime un qualcosa
 * successo nel passato, e racchiude i dati che lo rappresentano.
 *
 * @author rascioni
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = As.PROPERTY, property = "_type")
public interface DomainEvent {

}
