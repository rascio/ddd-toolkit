package it.r.dddtoolkit.es.ddd;

import static it.r.dddtoolkit.es.ddd.EntityBehavior.behavior;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@EqualsAndHashCode(callSuper=true)
@ToString(callSuper=true)
public class TickTockState extends DomainEntityState<String>{

	private boolean tick = false;
	
	public TickTockState(String id) {
		super(id);
	}

	@Override
	protected EntityBehavior init() {
		return behavior()
			.when(TickEvent.class, new Behavior<TickEvent>() {
				@Override
				public void when(TickEvent event) {
					setTick(true);
				}
			})
			.when(TockEvent.class, new Behavior<TockEvent>() {
				@Override
				public void when(TockEvent event) {
					setTick(false);
				}
			});
	}
}
