package it.r.dddtoolkit.es.ddd;

public class TickTock extends EventSourcedDomainEntity<TickTockState, String>{
	
	protected TickTock(TickTockState state){
		super(state);
	}

	public TickTock(String id) {
		super(new TickTockState(id));
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
