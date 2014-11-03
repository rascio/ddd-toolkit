package it.r.dddtoolkit.es.ddd;

import static com.google.common.collect.FluentIterable.from;
import it.r.dddtoolkit.ddd.DomainEvent;
import it.r.dddtoolkit.es.ApplicationEvent;
import it.r.dddtoolkit.es.eventstore.EventStream;

import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.util.Map;

import lombok.RequiredArgsConstructor;

import com.google.common.base.Function;
import com.google.common.collect.Maps;

public class DomainEntityStateFactory {

    private static final DomainEntityStateFactory INSTANCE = new DomainEntityStateFactory();

    public static DomainEntityStateFactory getInstance() {
        return INSTANCE;
    }

    private static final Map<Class<?>, InnerDomainEntityStateFactory<?>> stateFactories = Maps.newConcurrentMap();

    @SuppressWarnings("unchecked")
    public <ID extends Serializable> DomainEntityState<ID> create(Class<?> domainEntityType, ID id, EventStream stream) {

        final InnerDomainEntityStateFactory<DomainEntityState<?>> stateClass = loadStateClassFactory(domainEntityType, id.getClass());

        final DomainEntityState<ID> state = (DomainEntityState<ID>) stateClass.create(id);

        state.loadFromHistory(from(stream.events()).transform(toDomainEvents()).toList(), stream.version());

        return state;
    }

    public static Function<ApplicationEvent<?>, DomainEvent> toDomainEvents() {
        return new Function<ApplicationEvent<?>, DomainEvent>() {

            @Override
            public DomainEvent apply(ApplicationEvent<?> input) {
                return input.getEvent();
            }

        };
    }

    @SuppressWarnings("unchecked")
    private <S> InnerDomainEntityStateFactory<S> loadStateClassFactory(Class<?> domainEntityClass, Class<?> idClass) {
        InnerDomainEntityStateFactory<S> c = (InnerDomainEntityStateFactory<S>) stateFactories.get(domainEntityClass);
        if (c == null) {
            try {

                c = new InnerDomainEntityStateFactory<S>(this.<S>stateFactory(domainEntityClass, idClass));

            } 
			catch (ClassNotFoundException | NoSuchMethodException | SecurityException e) {
                throw new RuntimeException(String.format("Can't find a suitable state constructor for class [%s]", domainEntityClass.getCanonicalName()), e);
            }
			stateFactories.put(domainEntityClass, c);
        }
        return c;
    }

    @SuppressWarnings("unchecked")
    private <S> Constructor<S> stateFactory(Class<?> entity, Class<?> idClass) throws ClassNotFoundException, NoSuchMethodException, SecurityException {
        final Class<?> domainEntityStateClass = Class.forName(entity.getCanonicalName() + "State");

        if (DomainEntityState.class.isAssignableFrom(domainEntityStateClass)) {
            return (Constructor<S>) domainEntityStateClass.getConstructor(idClass);
        }
        throw new RuntimeException(String.format("%s isn't a valid DomainEntityState class, it should have a constructor:\npublic %s(%s identity){\n\tsuper(identity)\n}", domainEntityStateClass.getName(), domainEntityStateClass.getSimpleName(), idClass.getName()));
    }

    @RequiredArgsConstructor
    private static class InnerDomainEntityStateFactory<S> {

        private final Constructor<S> constructor;

        public S create(Object id) {
            try {
                return constructor.newInstance(id);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }
}
