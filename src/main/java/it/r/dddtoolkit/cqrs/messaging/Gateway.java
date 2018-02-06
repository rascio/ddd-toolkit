package it.r.dddtoolkit.cqrs.messaging;

import it.r.dddtoolkit.cqrs.command.CommandHandler;

/**
 * Created by rascio on 04/02/18.
 */
public interface Gateway {

    CommandHandler command();
    //QueryHandler query();
}
