package it.r.dddtoolkit.cqrs.command;

/**
 * Il CommandDispatcher è il componente incaricato di smistare i Command verso il relativo CommandHandler
 * @author rascioni
 */
public interface CommandDispatcher {

    /**
     * Invia il comando al relativo CommandDispatcher
     * @param <E>
     * @param command
     * @return
     * @throws Exception 
     */
    <E> E dispatch(Command command) throws Exception;

    /**
     * Registra un nuovo command handler
     * @param handler 
     */
    void register(CommandHandler<? extends Command> handler);
}
