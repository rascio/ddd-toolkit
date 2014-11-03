package it.r.dddtoolkit.es;

import it.r.dddtoolkit.ddd.DomainEvent;

import java.util.Map;

import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Rappresenta un evento dell'applicazione, è utilizzato per aggiungere
 * informazioni non relative al dominio. Un evento dell'applicazione contiene
 * gli headers (customizabili in base all'applicazione) e un evento del dominio.
 *
 * @author rascioni
 * @param <E>
 */
@Data
@NoArgsConstructor(access = AccessLevel.PACKAGE)
public class ApplicationEvent<E extends DomainEvent> {

    public static final String AGGREGATE_ID = "x-aggregate-identity";
    public static final String OCCURED_ON = "x-event-occurred-on";
    public static final String CORRELATION_ID = "x-correlation-id";
    public static final String EVENT_IDENTIFIER = "x-event-identifier";
	public static final String VERSION = "x-version";

    public static <E extends DomainEvent> ApplicationEvent<E> of(E event, Map<String, Object> headers) {
        final ApplicationEvent<E> applicationEvent = new ApplicationEvent<E>();

        applicationEvent.setEvent(event);
        applicationEvent.setHeaders(headers);

        return applicationEvent;
    }

    private E event;
    private Map<String, Object> headers;

    public Object getHeader(String header) {
        return headers.get(header);
    }

}
