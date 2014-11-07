/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.r.dddtoolkit.es.ddd;

import it.r.dddtoolkit.ddd.DomainEvent;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

/**
 * Definisce il comportamento di un {@link DomainEntityState}.
 * Un'oggetto di questa classe può essere creato tramite il factory method {@link #behavior() }, al quale poi devono essere
 * poi registrati i vari {@link Behavior} utilizzando il metodo {@link #when(java.lang.Class, it.r.dddtoolkit.es.ddd.Behavior) }.
 * L'EntityBehavior gode di immutabilità, quindi ogni chiamata ad un metodo {@link #when(java.lang.Class, it.r.dddtoolkit.es.ddd.Behavior) } creerà una nuova istanza.
 * @author rascioni
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public abstract class EntityBehavior {
	
	public static EntityBehavior behavior() {
		return new NoBehavior();
	}
	/**
	 * Crea una nuova istanza che estende quella attuale, aggiungendo il {@link Behavior} che reagisce ad un evento di tipo <strong>E</strong>
	 * @param <E> Il tipo di evento che si vuole gestire.
	 * @param eventType
	 * @param behavior
	 * @return 
	 */
	public <E extends DomainEvent> EntityBehavior when(@NonNull Class<E> eventType, @NonNull Behavior<E> behavior) {
		
		return new BehavioredEntity(eventType, behavior, this);
	}
	
	/**
	 * Esegue l'evento e tenta la modifica dello stato.
	 * Nel caso l'evento non è gestibile tramite questo EntityBehavior verrà lanciata una <code>IllegalStateException</code>
	 * Per far ignorare un evento utilizzare il metodo {@link #ignore(java.lang.Class) }
	 * @param event 
	 */
	protected abstract void happened(DomainEvent event);
	
	/**
	 * Esplicita il fatto che l'evento viene gestisto, ma non causa modifiche.
	 * @param <E>
	 * @param eventType
	 * @return 
	 */
	public <E extends DomainEvent> EntityBehavior ignore(@NonNull Class<E> eventType) {
		return when(eventType, new Behavior<E>() {
			@Override
			public void when(E event) {
				//Do nothig....
			}
		});
	}

	private static final class NoBehavior extends EntityBehavior{
		@Override
		public void happened(DomainEvent event) {
			throw new IllegalStateException("Can't manage " + event.getClass().getName());
		}
	}
	
	@RequiredArgsConstructor
	private static final class BehavioredEntity<E extends DomainEvent> extends EntityBehavior {
		private final Class<E> eventType;
		private final Behavior<E> behavior;
		private final EntityBehavior tail;
		
		@Override
		public void happened(DomainEvent event) {
			if (eventType.equals(event.getClass())){
				behavior.when((E) event);
			}
			else {
				tail.happened(event);
			}
		}
	}
}
