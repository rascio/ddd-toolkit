/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.r.dddtoolkit.ddd.memory;

import com.google.common.collect.FluentIterable;
import it.r.dddtoolkit.ddd.DomainEntity;
import it.r.dddtoolkit.ddd.DomainRepository;
import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Implementazione semplice di un DomainRepository tramite una map java
 * @author rascioni
 * @param <E>
 * @param <I>
 */
public class InMemoryDomainRepository<E extends DomainEntity<I>, I extends Serializable> implements DomainRepository<E, I>{
    
    private final Map<I, E> store = Collections.synchronizedMap(new HashMap<I, E>());

    @Override
    public void store(E entity) {
        store.put(entity.identity(), entity);
    }

    @Override
    public E findByIdentity(I domainIdentity) {
        return store.get(domainIdentity);
    }

    @Override
    public boolean contains(I domainIdentity) {
        return store.containsKey(domainIdentity);
    }
    
    protected FluentIterable<E> entities(){
        return FluentIterable.from(store.values());
    }
}
