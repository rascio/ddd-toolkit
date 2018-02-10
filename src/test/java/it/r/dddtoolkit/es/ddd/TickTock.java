package it.r.dddtoolkit.es.ddd;

import it.r.dddtoolkit.core.Context;
import it.r.dddtoolkit.modules.es.ddd.EventSourcedAggregate;

public class TickTock extends EventSourcedAggregate<TickTockState, Context> {

	public TickTock(Context context) {
		super(new TickTockState(false), context);
	}

	public static TickTockState handle(TickEvent event, TickTockState state) {
		System.out.println("tick");
		return new TickTockState(true);
	}

	public static TickTockState handle(TockEvent event, TickTockState state) {
		System.out.println("tock");
		return new TickTockState(false);
	}
}
