package it.r.dddtoolkit.util;

import it.r.dddtoolkit.es.ddd.TickTock;
import it.r.dddtoolkit.es.ddd.TickTockState;
import org.junit.Test;

import static org.junit.Assert.*;

public class ReflectionsTest {

    @Test
    public void getGeneric() {
        assertEquals(TickTockState.class, Reflections.getGenericOfParent(TickTock.class, 0));
    }
}