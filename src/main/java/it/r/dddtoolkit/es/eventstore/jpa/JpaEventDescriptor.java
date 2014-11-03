package it.r.dddtoolkit.es.eventstore.jpa;

import javax.persistence.Basic;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import org.hibernate.annotations.Index;

@Getter
@ToString
@RequiredArgsConstructor
@NoArgsConstructor
public class JpaEventDescriptor<ID> {
	
	@Id @GeneratedValue
	private Long id;
	
	@Index(name="aggregateId")
	@NonNull
	private ID aggregateId;

	@NonNull
    protected Integer version;

    protected long created = System.currentTimeMillis();

    @Basic(fetch=FetchType.LAZY)
    @NonNull
    protected String event;

}
