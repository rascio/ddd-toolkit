package it.r.dddtoolkit.modules.es.eventstore;

import lombok.AllArgsConstructor;
import lombok.Value;

import java.time.Instant;

/**
 * Created by rascio on 03/02/18.
 */
@Value
@AllArgsConstructor(staticName = "of")
public class Version {

    public static Version UNINITIALIZED = Version.of(-1L, Instant.MIN);

    private Long number;
    private Instant timestamp;

    public Version next() {
        return Version.of(number + 1, Instant.now());
    }
}
