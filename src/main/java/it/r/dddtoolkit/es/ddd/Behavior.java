/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.r.dddtoolkit.es.ddd;

import it.r.dddtoolkit.ddd.DomainEvent;

/**
 * Definisce come un {@link DomainEntityState} reagisce ad un determinato (<code>E</code>) evento.
 * @author rascioni
 * @param <E> L'evento di dominio
 */
public interface Behavior<E extends DomainEvent>{
	void when(E event);
}
