package it.r.dddtoolkit.es.ddd;

import it.r.dddtoolkit.core.Context;
import it.r.dddtoolkit.modules.es.ddd.EventSourcedAggregate;

public class TickTock extends EventSourcedAggregate<TickTockState, Context> {
	
	public TickTock(String id) {
		super(id, new TickTockState(false));
	}

	public static TickTockState handle(TickEvent event, TickTockState state) {
		System.out.println("tick");
		return new TickTockState(true);
	}

	public static TickTockState handle(TockEvent event, TickTockState state) {
		System.out.println("tock");
		return new TickTockState(false);
	}
	
	public void tick(){
		if (state().isTick()) {
			throw new IllegalStateException("It's 'tock' time!");
		}
		this.apply(new TickEvent());
	}
	
	public void tock(){
		if (!state().isTick()) {
			throw new IllegalStateException("It's 'tick' time!");
		}
		this.apply(new TockEvent());
	}
}
