package it.r.dddtoolkit.cqrs.command;

import java.util.UUID;
import lombok.EqualsAndHashCode;

import lombok.RequiredArgsConstructor;
import lombok.Value;

/**
 * Rappresenta il contesto di esecuzione di un comando, offrendo un'id unico dell'esecuzione
 * e contiene parametri applicativi come ad esempio l'autenticazione.
 * @author rascioni
 */
@RequiredArgsConstructor
@EqualsAndHashCode
public abstract class CommandExecutionContext {

    public static final CommandExecutionContext ROOT = new RootExecutionContext();

    private final String executionId;

    public String executionId() {
        return this.executionId;
    }

    public abstract CommandExecutionContext parent();

    public abstract boolean isRoot();
    /*TODO Authentication authentication;*/

    protected CommandExecutionContext child() {
        return new NestedExecutionContext(this);
    }

    @Value
    @EqualsAndHashCode(callSuper = true)
    private static class RootExecutionContext extends CommandExecutionContext {

        private RootExecutionContext() {
            super("ROOT");
        }

        @Override
        public CommandExecutionContext parent() {
            throw new IllegalStateException("Can't get parent of root execution context. Check with isRoot() method.");
        }

        @Override
        public boolean isRoot() {
            return true;
        }
        
        @Override
        public String toString(){
            return "RootExecutionContext";
        }
    }

    @Value
    @EqualsAndHashCode(callSuper = true)
    private static class NestedExecutionContext extends CommandExecutionContext {

        CommandExecutionContext parent;

        public NestedExecutionContext(CommandExecutionContext parent) {
            super(UUID.randomUUID().toString());
            this.parent = parent;
        }

        @Override
        public CommandExecutionContext parent() {
            return parent;
        }

        @Override
        public boolean isRoot() {
            return false;
        }
        
        @Override
        public String toString() {
            return String.format("NestedExecutionContext[executionId=%s, parent=%s]", this.executionId(), this.parent());
        }
    }

}
