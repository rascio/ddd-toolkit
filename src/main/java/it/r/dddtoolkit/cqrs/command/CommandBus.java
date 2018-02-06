package it.r.dddtoolkit.cqrs.command;

import it.r.dddtoolkit.core.Context;

/**
 * Il CommandBus è il componente incaricato di smistare i Command verso il relativo CommandHandler
 * @author rascioni
 */
public interface CommandBus<C extends Context> {

    /**
     * Invia il comando al relativo CommandBus
     * @param command
     * @return
     * @throws Exception 
     */
    void handle(Command command, C context);
}
