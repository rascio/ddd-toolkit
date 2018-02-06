package it.r.dddtoolkit.modules.es.support;

import com.google.common.base.Throwables;
import it.r.dddtoolkit.core.DomainEvent;
import it.r.dddtoolkit.modules.es.ddd.EventSourcedAggregate;
import it.r.dddtoolkit.util.Reflections;
import lombok.AllArgsConstructor;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.util.function.BiFunction;

/**
 * Created by rascio on 04/02/18.
 */
@AllArgsConstructor
public class AggregateMutator<S> {

    public static <A extends EventSourcedAggregate<S, ?>, S> AggregateMutator<S> of(A aggregate) {
        return new AggregateMutator<>((Class<? extends EventSourcedAggregate<S, ?>>) aggregate.getClass());
    }

    private final Class<? extends EventSourcedAggregate<S, ?>> aggregateType;

    public <E extends DomainEvent> BiFunction<E, S, S> handlerFor(E event) {
        final Class<?> stateType = Reflections.getGenericOfParent(aggregateType, 0);

        final MethodHandle handle;
        try {
            handle = MethodHandles.lookup()
                .findStatic(aggregateType, "handle", MethodType.methodType(stateType, event.getClass(), stateType));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return (e, s) -> {
            try {
                return (S) handle.invoke(e, s);
            }
            catch (Throwable throwable) {
                Throwables.throwIfUnchecked(throwable);

                throw new RuntimeException(throwable);
            }
        };
    }

}
