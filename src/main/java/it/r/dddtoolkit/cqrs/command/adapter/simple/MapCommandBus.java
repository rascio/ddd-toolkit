package it.r.dddtoolkit.cqrs.command.adapter.simple;

import it.r.dddtoolkit.cqrs.command.Command;
import it.r.dddtoolkit.cqrs.command.CommandBus;
import it.r.dddtoolkit.cqrs.command.CommandHandler;
import it.r.dddtoolkit.core.Context;

import java.util.HashMap;
import java.util.Map;

/**
 * Implementazione semplice di un CommandBus, che basa il routing su una mappa.
 * @author rascioni
 */
public class MapCommandBus<C extends Context> implements CommandBus<C> {

    private final Map<Class<Command>, CommandHandler<Command, C>> handlers;

    private MapCommandBus(Map<Class<Command>, CommandHandler<Command, C>> handlers) {
        this.handlers = handlers;
    }

    @Override
    public void handle(Command command, C context) {

        final CommandHandler<Command, C> handler = handlers.get(command.getClass());

        handler.handle(command, context);
    }

    public static <C extends Context> Builder<C> builder() {
        return new Builder<>();
    }


    public static class Builder<C extends Context> {
        private final Map<Class<Command>, CommandHandler<Command, C>> handlers = new HashMap<>();

        public <CMD extends Command> Builder<C> register(Class<CMD> type, CommandHandler<CMD, C> handler) {
            handlers.put((Class<Command>) type, (CommandHandler<Command, C>) handler);
            return this;
        }

        public MapCommandBus<C> build() {
            return new MapCommandBus<>(this.handlers);
        }
    }

}
