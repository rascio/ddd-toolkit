package it.r.dddtoolkit.cqrs.command;

/**
 * Il CommandHandler è l'oggetto responsabile dell'esecuzione di un comando.
 * 
 * Il CommandHandler è l'entry-point verso il dominio dell'applicazione, e generalmente si occuperà di modificare un aggregate.
 *
 * @param <C> tipo del comando gestito
 */
public interface CommandHandler<C extends Command> {

    Object handle(C command) throws Exception;

}
