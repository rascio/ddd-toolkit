package it.r.dddtoolkit.cqrs.command;

import lombok.extern.slf4j.Slf4j;

/**
 * Holder for CommandExecutionContext
 * @author rascioni
 */
@Slf4j
public class CommandExecutionContextHolder {

    private final ThreadLocal<CommandExecutionContext> executionContexts = new ThreadLocal<CommandExecutionContext>() {

        @Override
        protected CommandExecutionContext initialValue() {
            return CommandExecutionContext.ROOT;
        }

    };

    public void initialize() {
        final CommandExecutionContext executionContext = executionContexts.get().child();
        executionContexts.set(executionContext);
        log.debug("Setting up {} for thread {}", new Object[]{executionContext, Thread.currentThread().getName()});
        executionContexts.set(executionContext);
    }

    public CommandExecutionContext actual() {
        return executionContexts.get();
    }

    public CommandExecutionContext reset() {
        final CommandExecutionContext cec = executionContexts.get();
        executionContexts.set(cec.parent());
        log.debug("Resetting to context {} for thread {}", new Object[]{cec, Thread.currentThread().getName()});
        return cec;
    }
}
