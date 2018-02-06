/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.r.dddtoolkit.ddd.memory;

import com.google.common.collect.FluentIterable;
import it.r.dddtoolkit.ddd.Aggregate;
import it.r.dddtoolkit.ddd.DomainRepository;
import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Implementazione semplice di un DomainRepository tramite una map java
 * @author rascioni
 * @param <A>
 */
public class InMemoryDomainRepository<A extends Aggregate<?>> implements DomainRepository<A>{
    
    private final Map<String, A> store = Collections.synchronizedMap(new HashMap<String, A>());

    @Override
    public void store(A entity) {
        store.put(entity.identity(), entity);
    }

    @Override
    public A findByIdentity(String domainIdentity) {
        return store.get(domainIdentity);
    }

    @Override
    public boolean contains(String domainIdentity) {
        return store.containsKey(domainIdentity);
    }
    
    protected FluentIterable<A> entities(){
        return FluentIterable.from(store.values());
    }
}
