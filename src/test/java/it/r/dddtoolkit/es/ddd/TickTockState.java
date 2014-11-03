package it.r.dddtoolkit.es.ddd;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@EqualsAndHashCode(callSuper=true)
@ToString(callSuper=true)
public class TickTockState extends DomainEntityState<String>{

	private boolean tick = true;
	
	public TickTockState(String id) {
		super(id);
	}
	
	public void when(TickEvent event) {
		this.tick = false;
	}
	
	public void when(TackEvent event) {
		this.tick = true;
	}
}
