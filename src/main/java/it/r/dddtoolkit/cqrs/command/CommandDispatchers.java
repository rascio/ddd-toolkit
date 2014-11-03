package it.r.dddtoolkit.cqrs.command;

import lombok.extern.slf4j.Slf4j;

/**
 * Classe di utils per creare oggetti di supporto al CommandDispatcher
 * @author rascioni
 */
@Slf4j
public class CommandDispatchers {
    
    /**
     * Crea un CommandDispatcher che logga tutti i comandi attraverso Slf4J, delegando la reale
     * esecuzione al CommandDispatcher passato.
     * @param commandDispatcher
     * @return 
     */
    public static CommandDispatcher slf4j(final CommandDispatcher commandDispatcher) {
        return new CommandDispatcher() {

            @Override
            public <E> E dispatch(Command command) throws Exception {
                log.debug("Dispatching command: {}", command);
                return commandDispatcher.dispatch(command);
            }

            @Override
            public void register(CommandHandler<? extends Command> handler) {
                commandDispatcher.register(handler);
            }
            
            
        };
    }
    
    /**
     * Crea un CommmandDispatcher che non fa nulla
     * @return 
     */
    public static CommandDispatcher muted() {
        return new CommandDispatcher() {

            @Override
            public <E> E dispatch(Command command) throws Exception {
                log.info("Muted {}", command);
                return null;
            }

            @Override
            public void register(CommandHandler<? extends Command> handler) {
            }
            
        };
    }
}
