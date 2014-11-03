package it.r.dddtoolkit.es.ddd.interceptors;

import it.r.dddtoolkit.cqrs.command.CommandExecutionContext;
import it.r.dddtoolkit.cqrs.command.CommandExecutionContextHolder;
import it.r.dddtoolkit.ddd.DomainEvent;
import it.r.dddtoolkit.es.ApplicationEvent;
import it.r.dddtoolkit.es.ddd.PreStoreEventInterceptor;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class CorrelatedCommandIdPreStoreEventInterceptor implements PreStoreEventInterceptor{

	public static final String CORRELATED_ID = "x-event-correlatedId";
	private final CommandExecutionContextHolder contextHolder;
        
	@Override
	public void process(ApplicationEvent<DomainEvent> event) {
            final CommandExecutionContext executionContext = contextHolder.actual();
            
            event.getHeaders().put(CORRELATED_ID, executionContext.executionId());
	}
}
