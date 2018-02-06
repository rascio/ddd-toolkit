package it.r.dddtoolkit.es.support;

import it.r.dddtoolkit.es.ddd.TickEvent;
import it.r.dddtoolkit.es.ddd.TickTock;
import it.r.dddtoolkit.es.ddd.TickTockState;
import it.r.dddtoolkit.modules.es.support.AggregateMutator;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by rascio on 04/02/18.
 */
public class AggregateMutatorTest {

    @Test
    public void testHandlerFor() {
        AggregateMutator<TickTockState> mutator = AggregateMutator.of(new TickTock(""));

        final TickEvent event = new TickEvent();
        final TickTockState result = mutator.handlerFor(event)
            .apply(event, new TickTockState(false));

        assertTrue(result.isTick());
    }

}