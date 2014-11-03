package it.r.dddtoolkit.cqrs.command.adapter.simple;

import static com.google.common.base.Predicates.not;
import static com.google.common.collect.FluentIterable.from;
import it.r.dddtoolkit.cqrs.command.Command;
import it.r.dddtoolkit.cqrs.command.CommandDispatcher;
import it.r.dddtoolkit.cqrs.command.CommandExecutionContextHolder;
import it.r.dddtoolkit.cqrs.command.CommandHandler;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.common.base.Predicate;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Implementazione semplice di un CommandDispatcher, che basa il routing su una mappa.
 * @author rascioni
 */
@RequiredArgsConstructor
public class MapCommandDispatcher implements CommandDispatcher {

    @Getter
    private final CommandExecutionContextHolder executionContextHolder;

    private final Map<Class<Command>, CommandHandler<Command>> handlers = new HashMap<>();

    @Override
    public <E> E dispatch(Command command) throws Exception {

        executionContextHolder.initialize();

        final CommandHandler<Command> handler = handlers.get(command.getClass());
        try {
            return (E) handler.handle(command);
        } 
        catch (ClassCastException e) {
            throw new IllegalArgumentException(String.format("Command handler [%s] doesn't return the expected type", handler.getClass().getSimpleName()));
        }
    }
    
    @Override
    public void register(CommandHandler<? extends Command> handler) {
        handlers.put(handledType(handler.getClass()), (CommandHandler<Command>) handler);
    }

    @SuppressWarnings("unchecked")
    private Class<Command> handledType(Class<?> handlerClass) {
        final Method handleMethod = retrieveHandleMethod(handlerClass);
        return (Class<Command>) handleMethod.getParameterTypes()[0];
    }

    private <C extends Command> Method retrieveHandleMethod(Class<?> handlerClass) {
        return from(methods(handlerClass))
                .filter(method("handle"))
                .filter(not(param(Command.class)))
                .first()
                .get();
    }

    private Predicate<Method> param(final Class<Command> class1) {
        return new Predicate<Method>() {
            @Override
            public boolean apply(Method input) {
                return input.getParameterTypes().length > 0 && input.getParameterTypes()[0].isAssignableFrom(class1);
            }
        };
    }

    private Predicate<? super Method> method(final String name) {
        return new Predicate<Method>() {
            @Override
            public boolean apply(Method input) {
                return input.getName().equals(name);
            }
        };
    }

    private List<Method> methods(Class<?> handlerClass) {
        return Arrays.asList(handlerClass.getMethods());
    }

}